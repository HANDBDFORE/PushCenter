package com.hand.push.core.domain;

import com.hand.push.core.Pusher;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 10:54 AM
 */
public class AppChannel {

    private final List<Pusher> pushers;
    private final String appName;
    private final String appSecret;

    public AppChannel(List<Pusher> pushers, String appName, String appSecret) {
        check(pushers, appName, appSecret);
        this.pushers = pushers;
        this.appName = appName;
        this.appSecret = appSecret;

    }

    private void check(List<Pusher> pushers, String appName, String appSecret) {

        if (appName == null || appName.trim().length() == 0)
            throw new IllegalArgumentException("PushApp 的 appName 必须声明 ");
        if (pushers == null || pushers.size() == 0)
            throw new IllegalArgumentException("PushApp: " + appName + " 属性pushers必须包含至少一个pusher");
        if (appSecret == null || appSecret.trim().length() == 0)
            throw new IllegalArgumentException("PushApp: " + appName + " 属性appSecret不能空，用于匹配");
    }

    public List<Pusher> getPushers() {
        return Collections.unmodifiableList(pushers);
    }

    public String getAppName() {
        return appName;
    }

    public String getAppSecret() {
        return appSecret;
    }

    @Override
    public String toString() {
        return "PushApp{" +
                "pushers=" + pushers +
                ", appName='" + appName + '\'' +
                ", appSecret='" + appSecret + '\'' +
                '}';
    }
}
