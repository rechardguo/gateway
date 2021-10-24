package rechard.learn.gw.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.exception.UnknownServiceException;
import rechard.learn.gw.plugin.AuthCommand;

/**
 * jwt认证
 * @author Rechard
 **/
public class JWTAuthCommand implements AuthCommand {
    @Override
    public boolean doValid(ServerHttpRequest request) throws UnknownServiceException {
        //todo
        return true;
    }
}
