package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public class BadCredentialsException extends AuthenticationException {

    public BadCredentialsException(String msg) {
        super(msg);
    }
}

