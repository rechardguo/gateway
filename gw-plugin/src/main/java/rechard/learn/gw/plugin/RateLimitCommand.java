package rechard.learn.gw.plugin;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.exception.AuthenticationException;
import rechard.learn.gw.exception.RateLimitException;

import java.util.Map;

/**
 * 限流命令接口
 * @author Rechard
 **/
public interface RateLimitCommand {
    boolean isAllowed(ServerHttpRequest request) throws RateLimitException;


    /**
     * 留给plugin使用
     * @param request
     * @param config 预想将来可能会有管理平台配置的一些参数，放到config里
     * @return
     * @throws AuthenticationException
     */
    default boolean isAllowed(ServerHttpRequest request, Map<String,String> config)
            throws RateLimitException{
        return true;
    };
}
