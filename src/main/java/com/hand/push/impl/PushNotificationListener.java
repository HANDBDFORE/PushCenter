/**
 * 
 */
package com.hand.push.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.reflect.TypeToken;
import com.hand.push.core.NotificationPush;
import com.hand.push.domain.UserPushToken;
import com.hand.push.util.GsonHelper;
import org.springframework.stereotype.Component;


/**
 * @author emerson
 * 
 */
@Component("ActionCommitListener")
public class PushNotificationListener implements MessageListener {

    @Resource(name = "PushCenter")
    private NotificationPush pushCenter;

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("++++++++++++++++++++++++++++++ receive " + ((TextMessage)message).getText());
        } catch (JMSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (message instanceof TextMessage) {
            try {
                List<UserPushToken> items = build((TextMessage) message);

                for (UserPushToken applyActionItem : items) {
                    pushCenter.putRecord(applyActionItem);
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private List<UserPushToken> build(TextMessage message) throws JMSException {
        System.out.println(message.getText());
        return GsonHelper.jsonToObject(message.getText(), new TypeToken<List<UserPushToken>>() {
        }.getType());
    }

}
