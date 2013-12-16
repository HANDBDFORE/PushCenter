package com.hand.push.impl.repository;

import com.hand.push.core.Pusher;
import com.hand.push.domain.UserPushToken;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 4:37 PM
 */
public abstract class AbsPusher implements Pusher {
    protected final JSONObject config;
    public AbsPusher(JSONObject configs){
       this.config = configs;
    }

}
