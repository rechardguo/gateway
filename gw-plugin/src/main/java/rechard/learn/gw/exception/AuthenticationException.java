package rechard.learn.gw.exception;

/**
 * @author Rechard
 **/
public abstract class AuthenticationException extends GatewayException {

    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs an {@code AuthenticationException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public AuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public AuthenticationException(String msg) {
        super(msg);
    }

}

