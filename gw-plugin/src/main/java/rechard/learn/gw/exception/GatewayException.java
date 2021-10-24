package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public class GatewayException extends RuntimeException {

    public GatewayException(String msg) {
        super(msg);
    }

    public GatewayException(String msg, Throwable t) {
        super(msg,t);
    }

}
