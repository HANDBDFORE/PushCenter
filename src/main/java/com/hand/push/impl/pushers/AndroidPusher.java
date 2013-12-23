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
import com.hand.push.core.Pusher;
import com.hand.push.core.domain.NodeResult;
import com.hand.push.dto.PushEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**  Android 推送器，使用 个推 平台
 * @author Emerson
 */

public final class AndroidPusher implements Pusher {
    private static final String RESULT_CODE_SUCCESS = "ok";
    private static final String RESULT_CODE_KEY = "result";

    private final String appid;
    private final String appkey;
    private final String masterSecret;
    private final String api;

    public AndroidPusher(String appid, String appkey, String masterSecret, String api) {

        check(appid, appkey, masterSecret, api);

        this.appid = appid;
        this.appkey = appkey;
        this.masterSecret = masterSecret;
        this.api = api;



        getLogger().debug("Android Pusher inited, " + toString());
    }

    private Logger getLogger() {
        return LogUtil.getThreadSafeCoreLogger();
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

        List<FutureTask<NodeResult>> tasks = new ArrayList<FutureTask<NodeResult>>(pushRequests.size());

        //并发推送
        for (PushEntry pushRequest : pushRequests) {
            FutureTask<NodeResult> task = putRecord(pushRequest);
            tasks.add(task);
            new Thread(task).start();
        }


        //获取结果
        for (FutureTask<NodeResult> task : tasks) {
            try {
                //获取执行结果
                NodeResult pushResult = task.get();

                //如果有错，添加到错误列表
                if (pushResult.hasError())
                    result.addErrors(pushResult.getErrorList());

            } catch (Exception e) {
                //未知异常
                e.printStackTrace();
                result.addError(e.getMessage(), AndroidPusher.class);
            }
        }

        if (result.hasError())
            getLogger().error("An error occurred when pushing to Android devices: " + result.getErrorList().toString());
        else {
            getLogger().info("Android Pusher processes ended, no error occurred");
        }

        return result;


    }

    /**
     * 将推送请求转化为一个FutureTask
     *
     * @param entry
     * @return
     */
    private FutureTask<NodeResult> putRecord(final PushEntry entry) {

        return new FutureTask<NodeResult>(
                new Callable<NodeResult>() {
                    @Override
                    public NodeResult call() throws Exception {

                        NodeResult executeResult = null;


                        NotificationTemplate template = generateNotificationTemplate(entry);
                        Target target = generateTarget(entry);
                        SingleMessage message = generateMessage(template);

                        IPushResult pushResult = pushMessage(target, message);

                        String responseCode = pushResult.getResponse().get(RESULT_CODE_KEY).toString();
                        if (RESULT_CODE_SUCCESS.equals(responseCode)) {
                            //推送成功

                            executeResult = NodeResult.success();
                            getLogger().trace("success: "+entry);

                        } else {
                            getLogger().error("error! Caused by: "+responseCode+", data: " +entry);
                            executeResult = NodeResult.error(responseCode, entry);
                        }

                        return executeResult;
                    }
                }

        );

    }


    /**
     * @param target1
     * @param message
     */
    private IPushResult pushMessage(Target target1, SingleMessage message) {
        IIGtPush push = new IGtPush(api, appkey, masterSecret);
        // 单推
        return push.pushMessageToSingle(message, target1);

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

}
