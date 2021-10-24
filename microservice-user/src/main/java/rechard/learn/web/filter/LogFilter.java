package rechard.learn.web.filter;

import lombok.extern.slf4j.Slf4j;
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
public class LogFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        long start = System.currentTimeMillis();
        log.info("request start:",request.getURI());
        //调用后
        return webFilterChain.filter(serverWebExchange).doFinally(s->{
            long rt = System.currentTimeMillis() - start;
            log.info("request end:",s);
            log.info(String.valueOf(serverWebExchange.getResponse().getStatusCode()));
        });
    }
}
