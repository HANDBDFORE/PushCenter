package com.hand.push.core;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:53 PM
 */
public class PusherNotFoundException extends RuntimeException {
    public PusherNotFoundException() {
    }

    public PusherNotFoundException(String message) {
        super(message);
    }

    public PusherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PusherNotFoundException(Throwable cause) {
        super(cause);
    }
}
