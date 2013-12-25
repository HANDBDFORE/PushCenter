package com.hand.push.core.repository;


import com.hand.push.core.AppNotFoundException;
import com.hand.push.core.domain.AppChannel;
import com.hand.push.core.dto.PushApp;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:11 PM
 */
public interface AppRegister {

    /**
     * 根据数据查询对应的app
     *
     * @param requestAppPacket
     * @return
     * @throws com.hand.push.core.AppNotFoundException 没找到app的时候，抛出此错误
     */
    public AppChannel loadApp(PushApp requestAppPacket) throws AppNotFoundException;
}
