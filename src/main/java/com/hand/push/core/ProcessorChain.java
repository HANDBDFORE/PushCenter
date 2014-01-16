package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;


/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 1:28 PM
 */

public class ProcessorChain {


    private final List<Processor> processors;


    public ProcessorChain(List<Processor> processors) {
        this.processors = processors;
        getLogger().debug("ProcessorChain init, " + processors.size() + " processors registered: " + toString());
    }

    public void process(final Bundle bundle) throws RejectedExecutionException {

        Logger logger = getLogger();
        logger.debug("ProcessorChain start to execute, bundle:" + bundle.toString());

        for (Processor processor : processors) {
            try {
                processor.process(bundle);
            } catch (Exception e) {
                //当一个节点出现问题时，采取继续执行的策略，以便后续节点能够执行
                logger.error("An unexpected exception occurred, we don't have any idea. Caused By: " + e);
            }
        }


        logger.info("Chain Execution ended");
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @Override
    public String toString() {
        return "ProcessorChain{" +
                "processors=" + processors +
                '}';
    }
}
