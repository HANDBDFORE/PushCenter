package com.hand.push.impl.service;

import com.hand.push.core.*;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import com.hand.push.dto.PushEntry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/** 在节点这里就已经使用异步执行，各推送器不必担心自己会阻塞主线程
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

    //执行推送，避免卡掉主进程
    private static final ExecutorService EXECUTOR;

    static {
        EXECUTOR = Executors.newCachedThreadPool();
    }


    @Override
    public NodeResult process(Bundle bundle) {
        NodeResult result = new NodeResult();

        //1.根据app，找到推送配置
        PushApp requestAppPacket = bundle.getPushPacket().getApp();

        //2.获取推送器
        App app = register.loadApp(requestAppPacket);


        //3. 将推送分组
        Map<String, List<PushEntry>> groupedRequest = groupRequests(bundle.getPushPacket().getData());


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
            } catch (Exception otherE) {
                result.addError("发生未知错误 " + otherE.getMessage(), pushRequests);
            }

        }

        return result;
    }

    private NodeResult push(final List<PushEntry> requests, final Pusher pusher) {
       final NodeResult result = new NodeResult();


        if (EXECUTOR.isShutdown()) {
            result.addError("PushProcessor 试图在关闭系统期间继续执行新推送请求",new Object());
            return result;
        }

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                NodeResult pushResult = pusher.push(requests);
                for (ErrorEntry errorEntry : pushResult.getErrorList()) {
                    //因为是异步任务，前台并不需要立即知道结果，所以丢弃push过程中的错误，转为日志记录
                    //TODO 记录日志
                    //TODO 思考：如果需要将错误的数据记录数据库之类，应该在这里留一个函数回调
                    System.out.println(errorEntry.getMessage()+" "+errorEntry.getData());
                }

            }
        });

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

    private Pusher selectPusher(String platformName, List<Pusher> pushers) throws PusherNotFoundException{
        for (Pusher pusher : pushers) {
            if (pusher.tellMeYourDeviceType().equals(platformName))
                return pusher;
        }

        throw new PusherNotFoundException("未找到要求的推送器，请检查配置 " + platformName);

    }


}
