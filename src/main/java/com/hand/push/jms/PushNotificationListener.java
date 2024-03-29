/**
 *
 */
package com.hand.push.jms;

import com.hand.push.core.ProcessorChain;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.dto.PushRequest;
import com.hand.push.util.JsonHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


/**
 * @author emerson
 */
@Component("PushNotificationListener")
public class PushNotificationListener implements MessageListener {

    @Resource(name = "processorChain")
    private ProcessorChain processor;

    @Override
    public void onMessage(Message message) {

        if (message instanceof TextMessage) {
            try {

                PushRequest packet = build((TextMessage) message);
                Bundle bundle = new BundleImpl(packet, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));
                processor.process(bundle);

            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("请发送textMessage");
        }
    }

    private PushRequest build(TextMessage message) throws JMSException {

        System.out.println(message.getText());


        if (StringUtils.isEmpty(message.getText())) {
            //TODO 记录日志
            throw new IllegalArgumentException("消息不能为空");
        }
        String itemsString = message.getText().trim();

        return JsonHelper.stringToJson(itemsString, PushRequest.class);
    }

}
