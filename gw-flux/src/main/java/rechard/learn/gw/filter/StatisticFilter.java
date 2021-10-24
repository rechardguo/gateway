package rechard.learn.gw.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author Rechard
 **/
@Component
@Slf4j
@Order(100)
public class StatisticFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        long start = System.currentTimeMillis();
        log.info("上报kafka->接收到请求:");
        //doFinally表示调用返回后的记录
        return webFilterChain.filter(serverWebExchange).doFinally(s->{
            long rt = System.currentTimeMillis() - start;
            log.info("request end:");
            log.info(String.valueOf(serverWebExchange.getResponse().getStatusCode()));
        });
    }
}
