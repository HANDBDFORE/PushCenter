package com.hand.push.impl.repository;

import com.hand.push.core.AppNotFoundException;
import com.hand.push.core.LogUtil;
import com.hand.push.core.domain.AppChannel;
import com.hand.push.core.repository.AppRegister;
import com.hand.push.core.dto.PushApp;
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

    private final List<AppChannel> appChannels;

    private static Logger coreLogger = LogUtil.getThreadSafeCoreLogger();

    @Autowired(required = true)
    public AppRegisterSpringImpl(List<AppChannel> appChannels) {
        check(appChannels);
        coreLogger.debug(this.getClass() + " init, appChannels size: " + appChannels.size() + ", appChannels are: " + appChannels);
        this.appChannels = appChannels;
    }

    private void check(List<AppChannel> appChannels) {
        if (CollectionUtils.isEmpty(appChannels)) throw new IllegalArgumentException("app列表为空，请进行注册");
    }


    public AppChannel loadApp(PushApp packetRequestApp) {
        coreLogger.debug("I'm going to find appChannel by criteria: " + packetRequestApp);

        for (AppChannel appChannel : appChannels) {
            if (appChannel.getAppName().equals(packetRequestApp.getKey()) && appChannel.getAppSecret().equals(packetRequestApp.getSecret())) {
                coreLogger.debug("found corresponding appChannel: " + appChannel);
                return appChannel;
            }

        }

        coreLogger.error("Cannot find corresponding appChannel: " + packetRequestApp);
        throw new AppNotFoundException("AppRegisterSpringImpl 未找到需要的app: " + packetRequestApp);
    }


}
