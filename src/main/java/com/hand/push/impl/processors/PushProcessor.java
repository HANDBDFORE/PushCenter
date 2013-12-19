package com.hand.push.impl.processors;

import com.hand.push.core.LogUtil;
import com.hand.push.core.Processor;
import com.hand.push.core.Pusher;
import com.hand.push.core.PusherNotFoundException;
import com.hand.push.core.domain.App;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.NodeResult;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import com.hand.push.dto.PushEntry;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将请求数据进行集中推送
 *
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
    public NodeResult process(Bundle bundle) {
        logger.debug("PushProcessor received bundle");


        //构造空结果
        NodeResult result =NodeResult.empty();

        //1.根据app，找到推送配置
        PushApp requestAppPacket = bundle.getPushPacket().getApp();

        //2.获取推送器
        App app = register.loadApp(requestAppPacket);
        logger.trace("app acquired :"+app);


        //3. 将推送分组
        Map<String, List<PushEntry>> groupedRequest = groupRequests(bundle.getPushPacket().getEntries());
        logger.trace("grouped push requests: "+groupedRequest);


        //4. 选择推送器推送
        for (String platform : groupedRequest.keySet()) {
            //选一组数据
            List<PushEntry> pushRequests = groupedRequest.get(platform);
            try {
                //选择推送器
                Pusher pusher = selectPusher(platform, app.getPushers());

                //推送
                NodeResult pushResult = push(pushRequests, pusher);

                //收集错误
                result.addErrors(pushResult.getErrorList());

            } catch (PusherNotFoundException pne) {
                result.addError(pne.getMessage(), pushRequests);
            } catch (RuntimeException otherE) {
                //捕获未知错误，收集数据返回
                logger.error("An unexpected error occurred, I've no idea: "+otherE.getCause());
                result.addError("发生未知错误 " + otherE.getMessage(), pushRequests);
            }

        }

        logger.trace("process end");

        return result;
    }

    private NodeResult push(final List<PushEntry> requests, final Pusher pusher) {
        final NodeResult result = new NodeResult();

        NodeResult pushResult = pusher.push(requests);

        if (pushResult.hasError())
            result.addErrors(pushResult.getErrorList());

        return result;
    }

    /**
     * 将推送数据分组，形成
     * {
     *     "Android":[
     *      {"message":"..","token":".."},
     *      ...
     *     ],
     *
     *     "iphone":[
     *      {"message":"..","token":".."},
     *      ...
     *     ]
     * }
     *
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
     * @param platformName
     * @param pushers
     * @return
     * @throws PusherNotFoundException
     */
    private Pusher selectPusher(String platformName, List<Pusher> pushers) throws PusherNotFoundException {
        for (Pusher pusher : pushers) {
            if (pusher.tellMeYourDeviceType().equals(platformName))
                return pusher;
        }

        logger.error("Cannot find pusher by provided platformName:"+platformName+", please check your config.");
        throw new PusherNotFoundException("未找到要求的推送器，请检查配置 " + platformName);

    }


}
