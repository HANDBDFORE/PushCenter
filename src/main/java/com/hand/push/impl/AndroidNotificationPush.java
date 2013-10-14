/**
 * 
 */
package com.hand.push.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.hand.push.core.Pusher;
import com.hand.push.domain.UserPushToken;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

/**
 * @author Emerson
 * 
 */
@Component("AndroidNotificationPush")
public class AndroidNotificationPush implements Pusher, DisposableBean, InitializingBean {
    @Value("${androidpush.appid}")
    private String appid;

    @Value("${androidpush.appkey}")
    private String appkey;

    @Value("${androidpush.mastersecret}")
    private String mastersecret;

    @Value("${androidpush.api}")
    private String api;

    private static final ExecutorService EXECUTOR;

    static {
        EXECUTOR = Executors.newCachedThreadPool();

    }

    @Override
    public void putRecord(final UserPushToken ut) {

        if (ut == null || StringUtils.isEmpty(ut.getPushToken())) {
            return;
        }
        if (EXECUTOR.isShutdown()) {
            return;
        }

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                NotificationTemplate template = generateNotificationTemplate(ut.getCount());
                Target target = generateTarget(ut);
                SingleMessage message = generateMessage(template);
                pushMessage(target, message);
            }
        });
    }

    /**
     * @param target1
     * @param message
     */
    private void pushMessage(Target target1, SingleMessage message) {
        IIGtPush push = new IGtPush(api, appkey, mastersecret);
        // 单推
        IPushResult ret = push.pushMessageToSingle(message, target1);
        System.out.println(ret.getResponse().toString());
    }

    /**
     * @param template
     * @return
     */
    private SingleMessage generateMessage(NotificationTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        message.setOffline(true);
        // 用户当前不在线时,是否离线存储,可选
        message.setOfflineExpireTime(72 * 3600 * 1000); // 离线有效时间,单位为毫秒,可选
        return message;
    }

    /**
     * @param ut
     * @return
     */
    private Target generateTarget(UserPushToken ut) {
        Target target = new Target();
        target.setAppId(appid);
        target.setClientId(ut.getPushToken());
        return target;
    }

    /**
     * @return
     */
    private NotificationTemplate generateNotificationTemplate(int count) {
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appid);
        template.setAppkey(appkey);
        template.setTitle("新待办事项");
        template.setText("您有" + count + "条新审批");
        template.setLogo("");
        template.setTransmissionType(1);

        return template;
    }



    @Override
    public void destroy() throws Exception {
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(appkey)
                || StringUtils.isEmpty(mastersecret) || StringUtils.isEmpty(api)) {
            throw new IllegalStateException("android推送属性获取不正确，无法推送");
        }
    }


    @Override
    public String tellMeYourDeviceType() {
        return "Android";
    }
}
