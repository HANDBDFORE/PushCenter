package com.hand.push.core.service;

import com.hand.push.core.App;
import com.hand.push.core.PusherNotFoundException;
import com.hand.push.dto.PushApp;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:11 PM
 */
public interface AppRegister {


    public App loadApp(PushApp requestAppPacket) throws PusherNotFoundException;
}
