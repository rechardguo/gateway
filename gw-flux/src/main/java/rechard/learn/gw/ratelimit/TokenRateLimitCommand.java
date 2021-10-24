package rechard.learn.gw.ratelimit;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.exception.RateLimitException;
import rechard.learn.gw.plugin.RateLimitCommand;

/**
 * 令牌桶相关限流
 * @author Rechard
 **/
public class TokenRateLimitCommand implements RateLimitCommand {

    @Override
    public boolean isAllowed(ServerHttpRequest request) throws RateLimitException {
        //todo
        return true;
    }
}
