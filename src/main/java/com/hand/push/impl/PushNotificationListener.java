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
 * 
 */
@Component("PushNotificationListener")
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

        List<UserPushToken> tokens = null;

        if (StringUtils.isEmpty(message.getText())){
            throw new IllegalArgumentException("消息不能为空");
        }
        String itemsString = message.getText().trim();

        if (itemsString.charAt(0) == '[')
            tokens = JsonHelper.stringToCollection(itemsString, UserPushToken.class);
        else if (itemsString.charAt(0) == '{')
            tokens = Arrays.asList(JsonHelper.stringToJson(itemsString, UserPushToken.class));
        else
            throw new IllegalArgumentException("数据格式不正确");

        return tokens;
    }

}
