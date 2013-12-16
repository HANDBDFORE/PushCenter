package com.hand.push.impl;

import com.hand.push.core.Pusher;
import com.hand.push.core.annotation.Platform;
import com.hand.push.domain.UserPushToken;

import org.json.JSONObject;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:47 PM
 */
@Platform(platformName = "Android")
public class AndroidPusher implements Pusher {
    private final String appid;
    private final String appkey;
    private final String mastersecret;
    private final String api;

    public AndroidPusher(JSONObject config) {
        this.appid = config.getString("appid");
        this.appkey = config.getString("appKey");
        this.mastersecret = config.getString( "appSecret");
        this.api = config.getString("api");
    }


    @Override
    public String tellMeYourDeviceType() {
        return "Android";
    }

    @Override
    public void putRecord(UserPushToken ut) {

    }

    @Override
    public void push(List<UserPushToken> pushRequests) {
        throw new UnsupportedOperationException("未实现");
    }


    @Override
    public String toString() {
        return "AndroidPusher{" +
                "appid='" + appid + '\'' +
                ", appkey='" + appkey + '\'' +
                ", mastersecret='" + mastersecret + '\'' +
                ", api='" + api + '\'' +
                '}';
    }
}
