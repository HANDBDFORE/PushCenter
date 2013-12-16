package com.hand.push.impl;

import com.hand.push.core.Pusher;
import com.hand.push.core.annotation.Platform;
import com.hand.push.domain.UserPushToken;
import com.hand.push.impl.repository.AbsPusher;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:48 PM
 */
@Platform(platformName = "iPhone")
public class iOSPusher implements Pusher {

    private final String certificatePath;
    private final String certificatePassword;

    public iOSPusher(JSONObject config) {
        this.certificatePath = config.getString("p12FilPath");
        this.certificatePassword = config.getString("password");
    }

    @Override
    public String tellMeYourDeviceType() {
        return "iphone";
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
        return "iOSPusher{" +
                "certificatePath='" + certificatePath + '\'' +
                ", certificatePassword='" + certificatePassword + '\'' +
                '}';
    }
}
