package rechard.learn.gw.plugin;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.exception.AuthenticationException;

import java.util.Map;

/**
 * @author Rechard
 **/
public interface AuthCommand {
    boolean doValid(ServerHttpRequest request) throws AuthenticationException;


    /**
     * 留给plugin使用
     * @param request
     * @param config 预想将来可能会有管理平台配置的一些参数，放到config里
     * @return
     * @throws AuthenticationException
     */
    default boolean doValid(ServerHttpRequest request, Map<String,String> config)
            throws AuthenticationException{
        return true;
    };
}
