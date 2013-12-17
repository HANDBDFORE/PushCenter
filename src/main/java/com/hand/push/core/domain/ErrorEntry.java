package com.hand.push.core.domain;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 2:27 PM
 */
public class ErrorEntry {
    private final String message;
    private final Object data;

    public ErrorEntry(String message, Object data) {
        this.message = message;
        this.data = data;

    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ErrorEntry{" +
                "message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
