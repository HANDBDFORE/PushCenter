package com.hand.push.impl.repository;

import com.hand.push.core.LogUtil;
import com.hand.push.core.PusherNotFoundException;
import com.hand.push.core.domain.App;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 基于Spring的简单app查询组件
 *
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:13 PM
 */
@Repository("appRegisterSpringImpl")
public class AppRegisterSpringImpl implements AppRegister {

    private final List<App> apps;

    private static Logger coreLogger = LogUtil.getThreadSafeCoreLogger();

    @Autowired(required = true)
    public AppRegisterSpringImpl(List<App> apps) {
        check(apps);
        coreLogger.debug(this.getClass() + " init, apps size: " + apps.size() + ", apps are: " + apps);
        this.apps = apps;
    }

    private void check(List<App> apps) {
        if (CollectionUtils.isEmpty(apps)) throw new IllegalArgumentException("app列表为空，请进行注册");
    }


    public App loadApp(PushApp packetRequestApp) {
        coreLogger.debug("I'm going to find app by criteria: " + packetRequestApp);

        for (App app : apps) {
            if (app.getAppName().equals(packetRequestApp.getKey()) && app.getAppSecret().equals(packetRequestApp.getSecret())) {
                coreLogger.debug("found corresponding app: " + app);
                return app;
            }

        }

        coreLogger.error("Cannot find corresponding app: " + packetRequestApp);
        throw new PusherNotFoundException("AppRegisterSpringImpl 未找到需要的app: " + packetRequestApp);
    }


}
