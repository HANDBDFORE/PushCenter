package com.hand.push.core;

import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;

import java.util.List;

/**
 * 推送器
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 10/14/13
 * Time: 9:54 AM
 *
 */
public interface Pusher {
    /**
     * 告诉我，你能处理哪个平台
     * @return 平台名
     */
    public String tellMeYourDeviceType();

    /**
     * 推送数据
     * 注意，此方法<b>必须采用阻塞的方式进行推送</b>，每条推送结束之后，请将推送结果写入 {@code output}。
     * 以便后续节点对数据进行处理。
     *
     * @param pushRequests
     */
    public void push(List<PushEntry> pushRequests,Output output);
}
