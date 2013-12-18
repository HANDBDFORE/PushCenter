package com.hand.push.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/18/13
 * Time: 12:23 PM
 */
public class LogUtil {


    private static final ThreadLocal<Logger> systemLoggerThread = new ThreadLocal<Logger>();

    public static Logger getSystemLogger() {
        Logger logger = systemLoggerThread.get();

        if (logger == null) {
            synchronized (LogUtil.class) {
                Logger l = LoggerFactory.getLogger("dataFlow");

                systemLoggerThread.set(l);
                logger = l;

            }
        }

        return logger;


    }
}
