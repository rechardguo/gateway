package rechard.learn.gw.filter;

import cn.hutool.json.JSONUtil;
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
import rechard.learn.gw.constant.Constants;
import rechard.learn.gw.exception.InvalidRequestException;
import rechard.learn.gw.hystrix.HttpCommand;
import rechard.learn.gw.hystrix.HystrixBuilder;
import rechard.learn.gw.hystrix.HystrixConfig;
import rechard.learn.gw.utils.GWUtils;
import rx.Subscription;

import java.util.HashMap;
import java.util.Map;

/**
 * 转发服务
 * @author Rechard
 **/
@Component
@Slf4j
@Order(104)
public class RouteFilter implements WebFilter {
    private final WebClient client = WebClient.create();
    //从redis里来,这里写死
    private Map<String,String> serviceMap=new HashMap<>();

    public RouteFilter(){
        String config="{'ipList':'192.168.1.100,192.168.1.200','maxConcurrentRequests':'100','timeout':'3000','loadbalanceStretage':'roundrobin'}";
        serviceMap.put("orderService_config",config);
    }


    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        Flux<DataBuffer> body = request.getBody();
        String service = GWUtils.getService(request);

        if(service.equalsIgnoreCase("admin")){
            //交给其他的filter来处理
            return webFilterChain.filter(serverWebExchange);
        }

        String hystrixConf=serviceMap.get(service);
        HystrixConfig hystrixConfig=JSONUtil.toBean(hystrixConf, HystrixConfig.class);
        //设置groupkey为当前的服务名
        hystrixConfig.setGroupKey(service);
        String url=GWUtils.getURL(request);
        //设置某个url为commandkey
        hystrixConfig.setCommandKey(url);
        //创建出一个command
        HttpCommand command = new HttpCommand(hystrixConfig, serverWebExchange, webFilterChain);

        return Mono.create(s -> {
            Subscription sub = command.toObservable().subscribe(s::success,
                    s::error, s::success);
            s.onCancel(sub::unsubscribe);
            if (command.isCircuitBreakerOpen()) {
                log.error( "http:circuitBreaker is Open for service {} api {}"+hystrixConfig.getGroupKey() );
            }
        });
    }

}
