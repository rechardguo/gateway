package rechard.learn.gw.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import rechard.learn.gw.exception.UnknownServiceException;
import rechard.learn.gw.service.AuthService;
import rechard.learn.gw.utils.GWUtils;

/**
 * 认证功能
 * @author Rechard
 **/
@Component
@Slf4j
@Order(101)
public class AuthFilter implements WebFilter {

    @Autowired
    AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String service=GWUtils.getService(request);
        if(service.equals("acct")||service.equals("user")){
            if(authService.canAccess(request)){
                return webFilterChain.filter(serverWebExchange);
            }
        }
        return Mono.error(new UnknownServiceException(service));
    }
}
