package com.hand.push.core;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 11:14 AM
 */
public class RequestRefusedException extends RuntimeException{
    public RequestRefusedException() {
    }

    public RequestRefusedException(String message) {
        super(message);
    }

    public RequestRefusedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestRefusedException(Throwable cause) {
        super(cause);
    }
}
