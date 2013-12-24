/**
 *
 */
package com.hand.push.impl.pushers;

import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.hand.push.core.LogUtil;
import com.hand.push.core.PushFailureException;
import com.hand.push.core.domain.ErrorEntry;
import com.hand.push.core.domain.Output;
import com.hand.push.dto.PushEntry;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Android 推送器，使用 个推 平台
 *
 * @author Emerson
 */

public final class AndroidPusher extends AbstractConcurrentPusher {
    private static final String RESULT_CODE_SUCCESS = "ok";
    private static final String RESULT_CODE_KEY = "result";

    private final String appid;
    private final String appkey;
    private final String masterSecret;
    private final String api;
    private final IIGtPush iGtPush;

    public AndroidPusher(String appid, String appkey, String masterSecret, String api) {

        check(appid, appkey, masterSecret, api);

        this.appid = appid;
        this.appkey = appkey;
        this.masterSecret = masterSecret;
        this.api = api;

        iGtPush = new IGtPush(api, appkey, masterSecret);
        getLogger().debug("Android Pusher inited, " + toString());
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
    protected Runnable getTask(final PushEntry entry,final Output output) throws PushFailureException {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    NotificationTemplate template = generateNotificationTemplate(entry);
                    Target target = generateTarget(entry);
                    SingleMessage message = generateMessage(template);

                    IPushResult pushResult = iGtPush.pushMessageToSingle(message, target); //单推


                    String responseCode = pushResult.getResponse().get(RESULT_CODE_KEY).toString();
                    if (RESULT_CODE_SUCCESS.equals(responseCode)) {
                        //推送成功
                        output.addSuccessEntry(entry);
                        getLogger().trace("success: " + entry);


                    } else {
                        getLogger().error("error! Caused by: " + responseCode + ", data: " + entry);
                        output.addErrorEntry(new ErrorEntry(new PushFailureException("error! Caused by: " + responseCode), entry));
                    }
                } catch (Exception e) {
                    getLogger().error("error! An unexpected exception occurred, I've no idea: " + entry);
                    output.addErrorEntry(new ErrorEntry(new PushFailureException("error! An unexpected exception occurred, I've no idea: "), entry));
                }
            }
        };
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
    protected Logger getLogger() {
        return LogUtil.getThreadSafeCoreLogger();
    }


    @Override
    protected void cleanUp() {
        try {
            iGtPush.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
