package com.hand.push.impl.service;

import com.hand.push.core.ProcessorChain;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.dto.PushRequest;
import com.hand.push.core.service.PushChainService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 1/15/14
 * Time: 9:37 AM
 */
public class PushChainServiceImpl implements PushChainService {

    private static final int defaultWorkers = Runtime.getRuntime().availableProcessors() + 1;

    private static final AtomicInteger count = new AtomicInteger(0);

    private final ExecutorService executor;

    //并发执行处理链
    private final ProcessorChain processorChain;

    public PushChainServiceImpl(ProcessorChain processorChain) {
        this(processorChain, defaultWorkers, defaultWorkers * 2, 40000);
    }

    public PushChainServiceImpl(ProcessorChain processorChain, int coreWorkers, int emergencyWorkers, int queueCapacity) {

        executor = new ThreadPoolExecutor(coreWorkers, emergencyWorkers, 30, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(queueCapacity));
        this.processorChain = processorChain;

    }

    @Override
    public String accept(final PushRequest request) throws RejectedExecutionException {

        final Logger logger = getLogger();

        if (executor.isShutdown()) {
            logger.error("PushProcessor attempt to execute processors while shutting down, push requests REJECTED.");
            throw new RejectedExecutionException("系统正在关闭，拒绝执行");

        }


        final String jobId = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis()));
        logger.info("jobId generated: " + jobId);


        try {

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    System.out.println("========== " + count.incrementAndGet());

                    try {
                        logger.debug("dispatch a job to processor chain");
                        MDC.put("jobId", jobId);

                        Bundle bundle = new BundleImpl(request, jobId);

                        //处理
                        processorChain.process(bundle);

                        logger.info("Chain Execution ended");
                    } finally {
                        //清除
                        MDC.clear();
                        System.out.println(" end ========== " + count.get() +"  "+ ((ThreadPoolExecutor)executor).getQueue().size());

                    }

                }
            });

        } catch (RejectedExecutionException e) {
            throw e;

        } catch (Exception e) {
            //意外错误
            logger.error("Executor cannot be continued, Caused By: " + e);
            throw new RejectedExecutionException(e.getMessage());
        }


        return jobId;
    }

    @PreDestroy
    public void destroy() throws Exception {
        getLogger().debug("Receive shutdown message, ProcessorChain will end when running processor threads end. ");
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        getLogger().trace("ProcessorChain has shutdown.");
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
