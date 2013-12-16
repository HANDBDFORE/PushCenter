package com.hand.push.core;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:53 PM
 */
public class PusherNotfoundException extends RuntimeException {
    public PusherNotfoundException() {
    }

    public PusherNotfoundException(String message) {
        super(message);
    }

    public PusherNotfoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PusherNotfoundException(Throwable cause) {
        super(cause);
    }
}
