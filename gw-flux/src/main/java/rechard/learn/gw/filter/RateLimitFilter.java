package rechard.learn.gw.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import rechard.learn.gw.service.RateLimitService;

/**
 * 限流
 * @author Rechard
 **/
@Component
@Slf4j
@Order(103)
public class RateLimitFilter implements WebFilter {
    private RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        log.info("限流filter");
        //fill in logic...
        return webFilterChain.filter(serverWebExchange);
    }
}
