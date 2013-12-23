package com.hand.push.dto;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 4:59 PM
 */
public class PushEntry {
    private String platform;
    private String token;
    private int count;
    private String message;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushEntry pushEntry = (PushEntry) o;

        if (count != pushEntry.count) return false;
        if (!message.equals(pushEntry.message)) return false;
        if (!platform.equals(pushEntry.platform)) return false;
        if (!token.equals(pushEntry.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = platform.hashCode();
        result = 31 * result + token.hashCode();
        result = 31 * result + count;
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PushEntry{" +
                "platform='" + platform + '\'' +
                ", token='" + token + '\'' +
                ", count=" + count +
                ", message='" + message + '\'' +
                '}';
    }
}