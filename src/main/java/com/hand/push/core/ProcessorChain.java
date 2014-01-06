package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        getLogger().debug("ProcessorChain init, " + processors.size() + " processors registered: " + toString());
    }

    public void process(final Bundle bundle) throws RejectedExecutionException {

        if (EXECUTOR.isShutdown()) {
            getLogger().error("PushProcessor attempt to execute processors while shutting down, push requests REJECTED.");
            throw new RejectedExecutionException("系统正在关闭，拒绝执行");

        }


        try {

            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    getLogger().debug("ProcessorChain start to execute, bundle:" + bundle.toString());

                    for (Processor processor : processors) {
                        try {
                            processor.process(bundle);
                        } catch (Exception e) {
                            //当一个节点出现问题时，采取继续执行的策略，以便后续节点能够执行
                            getLogger().error("An unexpected exception occurred, we don't have any idea. Caused By: " + e.getMessage());
                        }
                    }


                    getLogger().info("Chain Execution ended");
                }
            });

        } catch (RejectedExecutionException e) {
            //TODO 如果不接受新请求，考虑如何处理
            throw e;

        } catch (Exception e) {
            //意外错误
            getLogger().error("Executor cannot be continued, Caused By: " + e.getCause());
            throw new RejectedExecutionException(e.getMessage());
        }
    }


    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    public void destroy() throws Exception {
        getLogger().debug("Receive shutdown message, ProcessorChain will end when running processor threads end. ");
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }

        getLogger().trace("ProcessorChain has shutdown.");
    }

    @Override
    public String toString() {
        return "ProcessorChain{" +
                "processors=" + processors +
                '}';
    }
}
