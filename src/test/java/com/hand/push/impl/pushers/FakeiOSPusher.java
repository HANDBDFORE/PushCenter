package com.hand.push.impl.pushers;

import com.hand.push.core.PushFailureException;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/25/13
 * Time: 2:59 PM
 */
public class FakeiOSPusher extends AbstractConcurrentPusher{
    @Override
    protected void cleanUp() {

    }



    @Override
    protected Runnable getTask(final PushEntry entry, Output output) throws PushFailureException {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    getLogger().debug("ios pusher push "+entry);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public String tellMeYourDeviceType() {
        return "iphone";
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
