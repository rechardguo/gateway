package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public class InvalidRequestException extends AuthenticationException {

    public InvalidRequestException(String msg) {
        super(msg);
    }
}
