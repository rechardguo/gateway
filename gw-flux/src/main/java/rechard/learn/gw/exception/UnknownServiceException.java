package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public class UnknownServiceException extends AuthenticationException {
    public UnknownServiceException(String msg) {
        super(msg);
    }
}
