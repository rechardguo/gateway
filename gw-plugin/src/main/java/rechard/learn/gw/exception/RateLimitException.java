package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public class RateLimitException extends GatewayException {
    public RateLimitException(String msg) {
        super(msg);
    }

    public RateLimitException(String msg, Throwable t) {
        super(msg, t);
    }
}
