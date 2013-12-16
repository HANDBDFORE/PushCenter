package com.hand.push.core.service;

import com.hand.push.core.Pusher;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:11 PM
 */
public interface AppRegester {

    public List<Pusher> load(String appName,String appSecret) throws IllegalArgumentException;
}
