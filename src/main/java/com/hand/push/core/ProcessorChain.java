package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.ProcessResult;
import org.slf4j.Logger;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static com.hand.push.core.domain.NodeResult.error;

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

    public ProcessResult process(final Bundle bundle) {
        final ProcessResult processResult = ProcessResult.construct(bundle.getJobId());

        if (EXECUTOR.isShutdown()) {
            processResult.addResult(error(new RequestRefusedException("PushProcessor 试图在关闭系统期间继续执行新推送请求"), bundle.getPushPacket().getEntries()));
            getThreadSafeCoreLogger().error("PushProcessor attempt to execute processors while shutting down, push requests REJECTED.");

        } else {

            try {
                EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        getThreadSafeCoreLogger().debug("ProcessorChain start to execute, bundle:" + bundle.toString());

                        for (Processor processor : processors) {
                            try {
                                processResult.addResult(processor.process(bundle));
                            } catch (Exception e) {
                                //当一个节点出现问题时，采取继续执行的策略，以便后续节点能够执行
                                getThreadSafeCoreLogger().error("An unexpected exception occurred, we don't have any idea. Caused By: " + e.getMessage());
                                processResult.addResult(error(e.getMessage(), processor));
                            }
                        }


                        if (processResult.hasError()) {
                            getThreadSafeCoreLogger().error("Execution ended, but there're something error happened during the execution: " + processResult.getErrors().toString());
                        } else {
                            getThreadSafeCoreLogger().info("Chain Execution ended, and nothing goes wrong");
                        }
                    }
                });

            }catch (RejectedExecutionException e){
                //TODO

            }catch (Exception e) {
                //意外错误
                getThreadSafeCoreLogger().error("Executor cannot be continued, Caused By: " + e.getCause());
                processResult.addResult(error(e.getMessage(), getClass()));
            }
        }


        return processResult;
    }

    private Logger getThreadSafeCoreLogger() {
        return LogUtil.getThreadSafeCoreLogger();
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
