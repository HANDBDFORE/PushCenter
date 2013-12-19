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

    public static Logger getThreadSafeCoreLogger() {
        Logger logger = systemLoggerThread.get();

        if (logger == null) {
            synchronized (LogUtil.class) {
                //这里还是有极小的可能性造成二次实例化，可用双重检查成例避免，
                // 但考虑到此实例化系统开销很小，而且对单例的需求不是特别严格，所以暂不使用
                Logger l = LoggerFactory.getLogger("core");

                systemLoggerThread.set(l);
                logger = l;
            }
        }

        return logger;


    }
}
