package com.hand.push.impl.service;

import com.hand.push.core.Bundle;
import com.hand.push.core.Processor;
import com.hand.push.core.Pusher;
import com.hand.push.core.service.AppRegester;
import com.hand.push.dto.PushPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:08 PM
 */
@Service
public class PushProcessor implements Processor{
    @Autowired
    private AppRegester regester;

    private Processor next;


    @Override
    public Object process(Bundle bundle) {



        //1.根据app，找到推送配置
        PushPacket.App app = bundle.getPushPacket().getApp();

        //2.获取推送器
        List<Pusher> pushers =  regester.load(app.getKey(), app.getSecret());


        for (PushPacket.PushRequest pushRequest :  bundle.getPushPacket().getData()) {
            for (Pusher pusher : pushers) {
                if (pushRequest.getPlatform().equals(pusher.tellMeYourDeviceType())){
                }
            }
        }

        if (next!=null)
            next.process(bundle);



        return new Object();
    }

    public void setNext(Processor next) {
        this.next = next;
    }
}
