package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 1:28 PM
 */

public class ProcessorChain {


    private final List<Processor> processors;


    //并发执行处理链
    private static final ExecutorService EXECUTOR;


    static {
        EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }


    public ProcessorChain(List<Processor> processors) {
        this.processors = processors;
        getThreadSafeCoreLogger().debug("ProcessorChain init, " + processors.size() + " processors registered: " + toString());
    }

    public void process(final Bundle bundle) throws RejectedExecutionException {

        if (EXECUTOR.isShutdown()) {
            getThreadSafeCoreLogger().error("PushProcessor attempt to execute processors while shutting down, push requests REJECTED.");
            throw new RejectedExecutionException("系统正在关闭，拒绝执行");

        }


        try {

            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    getThreadSafeCoreLogger().debug("ProcessorChain start to execute, bundle:" + bundle.toString());

                    for (Processor processor : processors) {
                        try {
                            processor.process(bundle);
                        } catch (Exception e) {
                            //当一个节点出现问题时，采取继续执行的策略，以便后续节点能够执行
                            getThreadSafeCoreLogger().error("An unexpected exception occurred, we don't have any idea. Caused By: " + e.getMessage());
                        }
                    }


                    getThreadSafeCoreLogger().info("Chain Execution ended");
                }
            });

        } catch (RejectedExecutionException e) {
            //TODO 如果不接受新请求，考虑如何处理
            throw e;

        } catch (Exception e) {
            //意外错误
            getThreadSafeCoreLogger().error("Executor cannot be continued, Caused By: " + e.getCause());
            throw new RejectedExecutionException(e.getMessage());
        }
    }


    private Logger getThreadSafeCoreLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @PreDestroy
    public void destroy() throws Exception {
        getThreadSafeCoreLogger().debug("Receive shutdown message, ProcessorChain will end when running processor threads end. ");
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }

        getThreadSafeCoreLogger().trace("ProcessorChain has shutdown.");
    }

    @Override
    public String toString() {
        return "ProcessorChain{" +
                "processors=" + processors +
                '}';
    }
}
