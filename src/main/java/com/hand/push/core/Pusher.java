package com.hand.push.core;

import com.hand.push.domain.UserPushToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 10/14/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Pusher{
    public String tellMeYourDeviceType();

    /**
     * 插入推送记录
     *
     * @param ut
     *            带插入记录
     */
    public void putRecord(UserPushToken ut);

    /**
     *  推送数据
     * @param pushRequests
     */
    public void push(List<UserPushToken> pushRequests);
}
