package com.hand.push.impl.pushers.test;

import com.hand.push.core.PushFailureException;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;
import com.hand.push.impl.pushers.AbstractConcurrentPusher;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 1/13/14
 * Time: 3:02 PM
 */
public class MockAndroidPusher extends AbstractTestPusher{

    @Override
    public String tellMeYourDeviceType() {
        return "mockAndroid";
    }
}
