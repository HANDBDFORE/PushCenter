/**
 *
 */
package com.hand.push.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.hand.push.core.NotificationPush;
import com.hand.push.domain.UserPushToken;
import com.hand.push.util.JsonHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author emerson
 */
@Component("PushNotificationListener")
public class PushNotificationListener implements MessageListener {

    @Resource(name = "PushCenter")
    private NotificationPush pushCenter;

    @Override
    public void onMessage(Message message) {
//        try {
//            System.out.println("++++++++++++++++++++++++++++++ receive " + ((TextMessage) message).getText());
//        } catch (JMSException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        if (message instanceof TextMessage) {
            try {
                List<UserPushToken> items = build((TextMessage) message);

                for (UserPushToken applyActionItem : items) {
                    try {
                        pushCenter.putRecord(applyActionItem);
                    } catch (RuntimeException e) {
                        //TODO 记录日志
                        e.printStackTrace();
                        //忽略继续
                    }
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private List<UserPushToken> build(TextMessage message) throws JMSException {

        System.out.println(message.getText());

        List<UserPushToken> tokens = null;

        if (StringUtils.isEmpty(message.getText())) {
            //TODO 记录日志
            throw new IllegalArgumentException("消息不能为空");
        }
        String itemsString = message.getText().trim();

        return JsonHelper.stringToCollection(itemsString, UserPushToken.class);
    }

}
