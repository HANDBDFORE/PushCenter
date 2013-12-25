package com.hand.push.impl.processors;

import com.hand.push.core.AppNotFoundException;
import com.hand.push.core.LogUtil;
import com.hand.push.core.Processor;
import com.hand.push.core.Pusher;
import com.hand.push.core.domain.AppChannel;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.dto.PushApp;
import com.hand.push.core.dto.PushEntry;
import com.hand.push.core.repository.AppRegister;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将请求数据进行集中推送
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:08 PM
 */
@Service("pushProcessor")
public class PushProcessor implements Processor {

    private final Logger logger = LogUtil.getThreadSafeCoreLogger();
    @Resource(name = "appRegisterSpringImpl")
    private AppRegister register;

    @Override
    public void process(Bundle bundle) {
        logger.debug("PushProcessor received bundle");

        //1.根据app，找到推送配置
        PushApp requestAppPacket = bundle.getPushPacket().getApp();

        //2.获取推送器
        AppChannel appChannel = register.loadApp(requestAppPacket);
        logger.trace("appChannel acquired :" + appChannel);


        //3. 将推送分组
        Map<String, List<PushEntry>> groupedRequest = groupRequests(bundle.getPushPacket().getEntries());
        logger.trace("grouped push requests: " + groupedRequest);


        //4. 选择推送器推送
        for (String platform : groupedRequest.keySet()) {
            //选一组数据
            List<PushEntry> pushRequests = groupedRequest.get(platform);
            try {
                //选择推送器
                Pusher pusher = selectPusher(platform, appChannel.getPushers());

                //推送
                pusher.push(pushRequests, bundle.getOutput());

            } catch (AppNotFoundException pne) {
                logger.error(pne.getMessage());
            } catch (RuntimeException otherE) {
                //捕获未知错误，收集数据返回
                logger.error("An unexpected error occurred, I've no idea: " + otherE.getCause());
            }

        }

        logger.trace("process end");

    }

    /**
     * 将推送数据分组，形成
     * {
     * "Android":[
     * {"message":"..","token":".."},
     * ...
     * ],
     * <p/>
     * "iphone":[
     * {"message":"..","token":".."},
     * ...
     * ]
     * }
     * <p/>
     * 格式
     *
     * @param rawRequests
     * @return
     */
    private Map<String, List<PushEntry>> groupRequests(List<PushEntry> rawRequests) {
        Map<String, List<PushEntry>> group = new HashMap<String, List<PushEntry>>();

        for (PushEntry rawRequest : rawRequests) {
            List<PushEntry> groupedRequests = group.get(rawRequest.getPlatform());
            if (groupedRequests == null) {
                groupedRequests = new ArrayList<PushEntry>();
                group.put(rawRequest.getPlatform(), groupedRequests);
            }

            groupedRequests.add(rawRequest);
        }

        return group;
    }

    /**
     * 根据推送数据，选出合适的推送器
     *
     * @param platformName
     * @param pushers
     * @return
     * @throws com.hand.push.core.AppNotFoundException
     */
    private Pusher selectPusher(String platformName, List<Pusher> pushers) throws AppNotFoundException {
        for (Pusher pusher : pushers) {
            if (pusher.tellMeYourDeviceType().equals(platformName))
                return pusher;
        }

        logger.error("Cannot find pusher by provided platformName:" + platformName + ", please check your config.");
        throw new AppNotFoundException("未找到要求的推送器，请检查配置 " + platformName);

    }


}
