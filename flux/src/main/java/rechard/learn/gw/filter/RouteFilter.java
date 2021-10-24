package rechard.learn.gw.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rechard.learn.gw.exception.InvalidRequestException;

/**
 * 转发服务
 * @author Rechard
 **/
@Component
@Slf4j
@Order(101)
public class RouteFilter implements WebFilter {
    private final WebClient client = WebClient.create();


    @Value("${url}")
    private String url;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        Flux<DataBuffer> body = request.getBody();
        String requestURI = request.getURI().getPath();


        int sl=-1;
        if((sl=requestURI.indexOf("/",1))==-1){
            return Mono.error(new InvalidRequestException());
        }
        String service=requestURI.substring(1,sl);
        //如果不是网关处理
        if(!service.equalsIgnoreCase("sample")){
            //交给其他的filter来处理
            return webFilterChain.filter(serverWebExchange);
        }

        //拼接url
        String leftUri=requestURI.substring(sl);
        String proxyURI=url+leftUri;
        System.out.println("转成新得uri-》"+proxyURI);

        //构建新的请求
        WebClient.RequestBodySpec req = client.method(request.getMethod())
                .uri(proxyURI)
                .headers(hs->{
                    if (headers != null) {
                        headers.forEach(
                                (h, vs) -> {
                                    hs.addAll(h, vs);
                                }
                        );
                    }
                });

        if (body != null) {
            if (body instanceof BodyInserter) {
                req.body((BodyInserter) body);
            } else if (body instanceof Flux) {
                Flux<DataBuffer> db = (Flux<DataBuffer>) body;
                req.body(BodyInserters.fromDataBuffers(db));
            } else {
                req.bodyValue(body);
            }
        }

        //发送请求
        Mono<ClientResponse> remoteResp = req.exchange();

       //异步写回
       return remoteResp.flatMap(resp->{
          //需要将resp 写回到client的response
          ServerHttpResponse clientResp =serverWebExchange.getResponse();
            clientResp.setStatusCode(resp.statusCode());
            HttpHeaders clientRespHeaders = clientResp.getHeaders();
            HttpHeaders remoteRespHeaders = resp.headers().asHttpHeaders();
            remoteRespHeaders.entrySet().forEach(
                    h -> {
                        String k = h.getKey();
                        clientRespHeaders.put(k, h.getValue());
                    }
            );
            return clientResp.writeWith(resp.body(BodyExtractors.toDataBuffers()))
                    .doOnError(throwable -> cleanup(resp))
                    .doOnCancel(() -> cleanup(resp));
        });
    }

    private void cleanup(ClientResponse clientResponse) {
        if (clientResponse != null) {
            clientResponse.bodyToMono(Void.class).subscribe();
        }
    }
}
