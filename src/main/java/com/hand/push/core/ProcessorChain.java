package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.NodeResult;
import com.hand.push.core.domain.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 1:28 PM
 */

public class ProcessorChain {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Processor> processors;


    //执行推送，避免卡掉主进程
    private static final ExecutorService EXECUTOR;


    static {
        EXECUTOR = Executors.newCachedThreadPool();
    }


    public ProcessorChain(List<Processor> processors) {
        this.processors = processors;

        logger.debug("ProcessorChain init, " + toString());
    }

    public ProcessResult process(final Bundle bundle) {
        final ProcessResult processResult = ProcessResult.construct();

        logger.info("receive execution request");

        if (EXECUTOR.isShutdown()) {
            processResult.addResult(NodeResult.error("PushProcessor 试图在关闭系统期间继续执行新推送请求", bundle.getPushPacket()));
            logger.error("PushProcessor attempt to execute processors while shutting down, push requests REJECTED.");
        } else {

            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    logger.trace("ProcessorChain start to execute, bundle:" + bundle.toString());

                    for (Processor processor : processors) {
                        try {
                            processResult.addResult(processor.process(bundle));
                        } catch (Exception e) {
                            //因为是异步任务，前台并不需要立即知道结果，所以丢弃push过程中的错误，转为日志记录
                            logger.error("An unexpected exception occurred, we don't have any idea. Caused By: " + e.getCause());
                            processResult.addResult(new NodeResult().addError(e.getMessage(), processor));
                        }
                    }
                }
            });

        }


        if (processResult.hasError()) {
            System.out.println(processResult);
            logger.error("Execution ended, but there're something error happened during the execution: " + processResult.getErrors().toString());
        } else {
            logger.info("Chain Execution ended, and nothing goes wrong");
            System.out.println("推送结束，没有错误发生");
        }


        return processResult;
    }

    @PreDestroy
    public void destroy() throws Exception {
        logger.trace("Receive shutdown message, ProcessorChain will end when running processor threads end. ");
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }

        logger.trace("ProcessorChain has shutdown.");
    }

    @Override
    public String toString() {
        return "ProcessorChain{" +
                "processors=" + processors +
                '}';
    }
}
