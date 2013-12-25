package com.hand.push.core.dto;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 4:58 PM
 */
public class PushApp {
    private String key;
    private String secret;

    public PushApp() {
    }

    public PushApp(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "PushApp{" +
                "key='" + key + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}