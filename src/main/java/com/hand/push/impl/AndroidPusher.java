/**
 *
 */
package com.hand.push.impl;

import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.hand.push.core.NodeResult;
import com.hand.push.core.Pusher;
import com.hand.push.dto.PushEntry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Emerson
 */

public final class AndroidPusher implements Pusher, DisposableBean {
    private final String appid;
    private final String appkey;
    private final String masterSecret;
    private final String api;

    private static final ExecutorService EXECUTOR;

    static {
        EXECUTOR = Executors.newCachedThreadPool();

    }

    public AndroidPusher(String appid, String appkey, String masterSecret, String api) {

        check(appid, appkey, masterSecret, api);

        this.appid = appid;
        this.appkey = appkey;
        this.masterSecret = masterSecret;
        this.api = api;
    }

    private void check(String appid, String appkey, String masterSecret, String api) {
        if (appid == null) throw new IllegalArgumentException("AndroidPusher 的 appid 属性必须赋值");
        if (appkey == null) throw new IllegalArgumentException("AndroidPusher 的 appkey 属性必须赋值");
        if (masterSecret == null) throw new IllegalArgumentException("AndroidPusher 的 masterSecret 属性必须赋值");
        if (api == null) throw new IllegalArgumentException("AndroidPusher 的 api 属性必须赋值");
    }


    @Override
    public String tellMeYourDeviceType() {
        return "Android";
    }


    @Override
    public NodeResult push(List<PushEntry> pushRequests) {

        NodeResult result = new NodeResult();


        for (PushEntry pushRequest : pushRequests) {
            try {
                this.putRecord(pushRequest);
            } catch (RuntimeException e) {
                result.addError(e.getMessage(), pushRequest);
            }
        }

        return result;


    }

    //    @Override
    private void putRecord(final PushEntry ut) {

        if (ut == null || StringUtils.isEmpty(ut.getToken())) {
            return;
        }
        if (EXECUTOR.isShutdown()) {
            return;
        }

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                NotificationTemplate template = generateNotificationTemplate(ut);
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
        IIGtPush push = new IGtPush(api, appkey, masterSecret);
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
    private Target generateTarget(PushEntry ut) {
        Target target = new Target();
        target.setAppId(appid);
        target.setClientId(ut.getToken());
        return target;
    }

    /**
     * @return
     */
    private NotificationTemplate generateNotificationTemplate(PushEntry ut) {
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appid);
        template.setAppkey(appkey);
        template.setTitle("新事项");
        template.setText(ut.getMessage());
        template.setLogo("");
        template.setTransmissionType(1);

        return template;
    }


    @Override
    public String toString() {
        return "AndroidPusher{" +
                "appid='" + appid + '\'' +
                ", appkey='" + appkey + '\'' +
                ", masterSecret='" + masterSecret + '\'' +
                ", api='" + api + '\'' +
                '}';
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


}
