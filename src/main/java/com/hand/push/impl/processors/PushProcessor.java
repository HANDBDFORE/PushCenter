package com.hand.push.impl.processors;

import com.hand.push.core.*;
import com.hand.push.core.domain.App;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.NodeResult;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import com.hand.push.dto.PushEntry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在节点这里就已经使用异步执行，各推送器不必担心自己会阻塞主线程
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:08 PM
 */
@Service("pushProcessor")
public class PushProcessor implements Processor {

//    private final Logger logger = new Logg

    @Resource(name = "appRegisterSpringImpl")
    private AppRegister register;

    @Override
    public NodeResult process(Bundle bundle) {
        NodeResult result = new NodeResult();

        //1.根据app，找到推送配置
        PushApp requestAppPacket = bundle.getPushPacket().getApp();

        //2.获取推送器
        App app = register.loadApp(requestAppPacket);


        //3. 将推送分组
        Map<String, List<PushEntry>> groupedRequest = groupRequests(bundle.getPushPacket().getEntries());


        //4. 选择推送器推送
        for (String platform : groupedRequest.keySet()) {
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
                //捕获未知错误，以便其他推送器可以继续执行
                result.addError("发生未知错误 " + otherE.getMessage(), pushRequests);
            }

        }

        return result;
    }

    private NodeResult push(final List<PushEntry> requests, final Pusher pusher) {
        final NodeResult result = new NodeResult();

        NodeResult pushResult = pusher.push(requests);

        if (pushResult.hasError())
            result.addErrors(pushResult.getErrorList());

        return result;
    }

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

    private Pusher selectPusher(String platformName, List<Pusher> pushers) throws PusherNotFoundException {
        for (Pusher pusher : pushers) {
            if (pusher.tellMeYourDeviceType().equals(platformName))
                return pusher;
        }

        throw new PusherNotFoundException("未找到要求的推送器，请检查配置 " + platformName);

    }


}
