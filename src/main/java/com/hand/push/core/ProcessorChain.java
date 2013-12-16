package com.hand.push.core;

import java.util.List;

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
    }

    public ProcessResult process(Bundle bundle) {
        final ProcessResult processResult = ProcessResult.construct();

        for (Processor processor : processors) {


            try {
                processResult.addResult(processor.process(bundle));
            } catch (Exception e) {
//                processResult.ad
                //TODO 捕获错误
                processResult.addResult(new NodeResult().addError(e.getMessage(), processor));
            }

        }

        System.out.println(processResult);

        return processResult;
    }


}
