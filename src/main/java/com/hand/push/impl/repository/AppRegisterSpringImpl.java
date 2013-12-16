package com.hand.push.impl.repository;

import com.hand.push.core.App;
import com.hand.push.core.PusherNotFoundException;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import com.hand.push.dto.PushRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:13 PM
 */
@Repository("appRegisterSpringImpl")
public class AppRegisterSpringImpl implements AppRegister {

    private final List<App> apps;

    @Autowired(required = true)
    public AppRegisterSpringImpl(List<App> apps) {
        this.apps = apps;
    }


    public App loadApp(PushApp packetRequestApp) {

        for (App app : apps) {
            if (app.getAppName().equals(packetRequestApp.getKey()) && app.getAppSecret().equals(packetRequestApp.getSecret()))
                return app;
        }

        throw new PusherNotFoundException("AppRegisterSpringImpl 未找到需要的app: " + packetRequestApp);
    }


}
