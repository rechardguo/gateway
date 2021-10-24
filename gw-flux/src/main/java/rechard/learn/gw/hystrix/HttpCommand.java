/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package rechard.learn.gw.hystrix;

import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;
import rechard.learn.gw.lb.IRule;
import rechard.learn.gw.lb.LoadBalancerBuilder;
import rechard.learn.gw.lb.Server;
import rechard.learn.gw.utils.GWUtils;
import rx.Observable;
import rx.RxReactiveStreams;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Optional;

/**
 * hystrix限流命令
 * @author rechard
 */
@Slf4j
public class HttpCommand extends HystrixObservableCommand<Void> {

    private static final WebClient WEB_CLIENT;
    private final ServerWebExchange exchange;
    private final WebFilterChain chain;
    private final String url;
    private final Integer timeout;
    private final HystrixConfig config;

    static {
        // configure tcp pool
        long acquireTimeout = Math.min(ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT, 3000);
        ConnectionProvider fixedPool = ConnectionProvider.fixed("soul-tcp-pool",
                ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS, acquireTimeout);
        TcpClient tcpClient = TcpClient.create(fixedPool);
        WEB_CLIENT = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    /**
     * Instantiates a new Http command.
     *
     * @param setter     the setter
     * @param exchange   the exchange
     * @param chain      the chain
     * @param url        the url
     * @param timeout    the timeout
     */
    public HttpCommand(final HystrixConfig config,
                       final ServerWebExchange exchange,
                       final WebFilterChain chain){
        super(HystrixBuilder.build(config));
        this.exchange = exchange;
        this.chain=chain;
        this.url = GWUtils.getURL(exchange.getRequest());
        this.timeout =  config.getTimeout();
        this.config = config;
    }

    @Override
    protected Observable<Void> construct() {
        return RxReactiveStreams.toObservable(doHttpInvoke());
    }

    private Mono<Void> doHttpInvoke() {
        ServerHttpRequest request = this.exchange.getRequest();
        HttpMethod method=request.getMethod();
        WebClient.RequestBodyUriSpec spec=WEB_CLIENT.method(method);
        //负载均衡策略使用
        String loadbalanceStretage = this.config.getLoadbalanceStretage();
        IRule rule = LoadBalancerBuilder.newBuilder().of(loadbalanceStretage).setIpList(this.config.getIpList()).build();
        //通过负载均衡策略找出一台server
        Server server=rule.choose();
        server.setScheme(request.getURI().getScheme());
        String uri=GWUtils.getURL(request);
        //拼接出新的uri
        //将新的uri设置回去
        spec.uri(reconstructURIWithServer(server,uri));
        return handleRequestBody(spec);
    }

    /**
     * 拼接出一个新的uri
     * @param server
     * @param uri
     * @return
     */
    public URI reconstructURIWithServer(Server server,  String uri) {
        String host = server.getHost();
        int port = server.getPort();
        String scheme = server.getScheme();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://");
            sb.append(host);
            if (port >= 0) {
                sb.append(":").append(port);
            }
            sb.append(uri);
            URI newURI = new URI(sb.toString());
            return newURI;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



    //降级处理
    @Override
    protected Observable<Void> resumeWithFallback() {
        return RxReactiveStreams.toObservable(doFallback());
    }


    private MediaType buildMediaType() {
        return MediaType.valueOf(Optional.ofNullable(exchange
                .getRequest()
                .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .orElse(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }


    private Mono<Void> doNext(final ClientResponse res) {
        //需要将resp 写回到client的response
        ServerHttpResponse clientResp =exchange.getResponse();
        clientResp.setStatusCode(res.statusCode());
        HttpHeaders clientRespHeaders = clientResp.getHeaders();
        HttpHeaders remoteRespHeaders = res.headers().asHttpHeaders();
        remoteRespHeaders.entrySet().forEach(
                h -> {
                    String k = h.getKey();
                    clientRespHeaders.put(k, h.getValue());
                }
        );
        return clientResp.writeWith(res.body(BodyExtractors.toDataBuffers()))
                .doOnError(throwable -> cleanup(res))
                .doOnCancel(() -> cleanup(res));
    }


    private void cleanup(ClientResponse clientResponse) {
        if (clientResponse != null) {
            clientResponse.bodyToMono(Void.class).subscribe();
        }
    }

    private Mono<Void> doFallback() {
        if (isFailedExecution()) {
            log.error( "http execute have error:{}", getExecutionException().getMessage());
        }
        final Throwable exception = getExecutionException();
        if (exception instanceof HystrixRuntimeException) {
            HystrixRuntimeException e = (HystrixRuntimeException) getExecutionException();
            if (e.getFailureType() == HystrixRuntimeException.FailureType.TIMEOUT) {
                exchange.getResponse().setStatusCode(HttpStatus.GATEWAY_TIMEOUT);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (exception instanceof HystrixTimeoutException) {
            exchange.getResponse().setStatusCode(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(config.getFallbackResp().getBytes())));
    }

    private Mono<Void> handleRequestBody(WebClient.RequestBodySpec requestBodySpec) {
        return requestBodySpec.headers(httpHeaders -> {
            httpHeaders.addAll(exchange.getRequest().getHeaders());
            httpHeaders.remove(HttpHeaders.HOST);
        })
                .contentType(buildMediaType())
                .body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody()))
                .exchange()
                .doOnError(e -> log.error(e.getMessage()))
                .timeout(Duration.ofMillis(timeout))
                .flatMap(this::doNext);
    }

}
