package com.hand.push.impl.pushers;import com.hand.push.core.LogUtil;import com.hand.push.core.Pusher;import com.hand.push.core.annotation.Platform;import com.hand.push.core.domain.ErrorRequestEntry;import com.hand.push.core.domain.Output;import com.hand.push.dto.PushEntry;import javapns.communication.exceptions.CommunicationException;import javapns.devices.Device;import javapns.devices.implementations.basic.BasicDevice;import javapns.notification.AppleNotificationServerBasicImpl;import javapns.notification.PushNotificationManager;import javapns.notification.PushNotificationPayload;import javapns.notification.PushedNotification;import org.slf4j.Logger;import javax.annotation.PreDestroy;import java.net.URL;import java.util.ArrayList;import java.util.List;import java.util.concurrent.CountDownLatch;import java.util.concurrent.ExecutorService;import java.util.concurrent.Executors;import java.util.concurrent.TimeUnit;@Platform(platformName = "iphone")//TODO 用模板方法重构public final class iOSPusher implements Pusher {    private static final String SOUND = "default";    private static ExecutorService executor = Executors.newCachedThreadPool();    private final String certificatePath;    private final String certificatePassword;    private final boolean isProduct;   // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务    public iOSPusher(String certificatePath, String certificatePassword, boolean isProduct) {        check(certificatePath, certificatePassword);        this.certificatePath = certificatePath;        this.certificatePassword = certificatePassword;        this.isProduct = isProduct;        getLogger().debug("iOS Pusher inited, " + toString());    }    private void check(String certificatePath, String certificatePassword) {        if (certificatePath == null) throw new IllegalArgumentException("iOSPusher 的 certificatePath 属性必须赋值");        if (certificatePassword == null) throw new IllegalArgumentException("iOSPusher 的 certificatePassword 属性必须赋值");    }    @Override    public String tellMeYourDeviceType() {        return "iphone";    }    @Override    public void push(List<PushEntry> pushRequests, Output output) {        getLogger().debug(getClass() + " called");        URL classPath = getClass().getClassLoader().getResource("");        String path = classPath.getPath() + certificatePath;        getLogger().trace("Full certificatePath: " + path);        CountDownLatch endGate = new CountDownLatch(pushRequests.size());        PushNotificationManager pushManager = new PushNotificationManager();        try {            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(path, certificatePassword, isProduct));            //并发推送            for (PushEntry pushRequest : pushRequests) {                executor.submit(putRecord(pushRequest, pushManager, output, endGate));            }        } catch (Exception e) {            e.printStackTrace();            getLogger().info("An unexpected exception occurred, I've no idea: " + e.getMessage());        } finally {            try {                pushManager.stopConnection();            } catch (Exception e) {                pushManager = null;            }        }        try {            //等待推送线程结束            endGate.await(2, TimeUnit.DAYS);            getLogger().info("iOS Pusher processes ended");        } catch (InterruptedException e) {            e.printStackTrace();            //TODO 详细记录            getLogger().error("Timeout: ");        }    }    private Runnable putRecord(final PushEntry ut, final PushNotificationManager pushManager, final Output output, final CountDownLatch endGate) {        return new Runnable() {            @Override            public void run() {                try {                    String alert = ut.getMessage();                    int badge = ut.getCount();                    PushNotificationPayload payLoad = new PushNotificationPayload(alert, badge, SOUND);                    // 发送push消息                    Device device = new BasicDevice();                    device.setToken(ut.getToken());                    PushedNotification notification = pushManager.sendNotification(device, payLoad, true);                    List<PushedNotification> notifications = new ArrayList<PushedNotification>();                    notifications.add(notification);                    List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);                    for (PushedNotification successfulNotification : successfulNotifications) {                        getLogger().trace("success: " + ut);                        output.addSuccessEntry(ut);                    }                    List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);                    for (PushedNotification failedNotification : failedNotifications) {                        getLogger().error("error! Caused by: " + failedNotification.getException() + ", data: " + ut);//                        pushResult.addError(failedNotification.getException().getMessage(), ut);                        output.addErrorEntry(new ErrorRequestEntry(failedNotification.getException(), ut));                    }                } catch (CommunicationException e) {                    e.printStackTrace();                    //TODO 可以考虑转换为IOException，包装在结果里                    getLogger().error("error! Caused by: " + e);//                    pushResult.addError(e.getMessage(),ut);                    output.addErrorEntry(new ErrorRequestEntry(e, ut));                } catch (Exception e) {                    getLogger().error("error! Caused by: " + e);                    e.printStackTrace();                    output.addErrorEntry(new ErrorRequestEntry(e, ut));//                    pushResult.addError(e.getMessage(),ut);                }finally {                    endGate.countDown();                }            }        };    }    private Logger getLogger() {        return LogUtil.getThreadSafeCoreLogger();    }    @Override    public String toString() {        return "iOSPusher{" +                "certificatePath='" + certificatePath + '\'' +                ", certificatePassword='" + certificatePassword + '\'' +                '}';    }    @PreDestroy    public void destroy() throws Exception {        getLogger().debug("Receive shutdown message, iOS Pusher will end when running processor threads end. ");        executor.shutdown();        try {            executor.awaitTermination(1, TimeUnit.HOURS);        } catch (InterruptedException e) {            executor.shutdownNow();        }        getLogger().trace("iOS Pusher has shutdown.");    }}