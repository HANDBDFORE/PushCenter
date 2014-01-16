package com.hand.push.impl.pushers.test;

import com.hand.push.core.PushFailureException;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;
import com.hand.push.impl.pushers.AbstractConcurrentPusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 1/13/14
 * Time: 3:07 PM
 */
public abstract class AbstractTestPusher extends AbstractConcurrentPusher{

    private static final String baseUrl = "http://10.213.212.54:8080/push";

    public AbstractTestPusher() {
        super(CORE_WORKERS, MAX_WORKERS, QUEUE_CAPACITY);
    }

    @Override
    protected void cleanUp() {

    }

    @Override
    protected Runnable getTask(final PushEntry entry,final Output output) throws PushFailureException {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(baseUrl+"/"+tellMeYourDeviceType());
                    URLConnection conn = url.openConnection();
                    InputStream inStream = conn.getInputStream();
                    int bytesum = 0;
                    int byteread = 0;

                    byte[] buffer = new byte[1204];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        System.out.println(bytesum);

                    }
                } catch (Throwable e) {
                    getLogger().error(e.toString());
                    output.addErrorEntry(entry,e);
                }

            }
        };
    }

    private Logger getLogger(){
       return LoggerFactory.getLogger(getClass());
    }



}
