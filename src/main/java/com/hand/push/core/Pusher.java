package com.hand.push.core;

import com.hand.push.core.domain.NodeResult;
import com.hand.push.dto.PushEntry;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 10/14/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Pusher {
    public String tellMeYourDeviceType();

    /**
     * 推送数据
     *
     * @param pushRequests
     */
    public NodeResult push(List<PushEntry> pushRequests);
}
