package com.hand.push.core;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 3:20 PM
 */
public class PushFailureException extends RuntimeException {
    public PushFailureException() {
    }

    public PushFailureException(String message) {
        super(message);
    }

    public PushFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public PushFailureException(Throwable cause) {
        super(cause);
    }
}
