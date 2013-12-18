package com.hand.push.core.domain;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 1:52 PM
 */
public class ProcessResult {
    private final String flowId;


    private final List<NodeResult> results;

    private ProcessResult(List<NodeResult> results,String flowId) {
        this.flowId =flowId;
        this.results = results;
    }

    public static ProcessResult construct(String flowId) throws IllegalArgumentException{
        if (StringUtils.isEmpty(flowId))throw new IllegalArgumentException("flowId必须赋值");
        return new ProcessResult(new ArrayList<NodeResult>(),flowId);
    }

    public ProcessResult addResult(NodeResult result) {
        this.results.add(result);
        return this;
    }

    public boolean hasError() {
        for (NodeResult result : results) {
            if (result.hasError()) return true;
        }
        return false;
    }

    public List<Object> getErrors() {
        List<Object> errors = new ArrayList<Object>();
        for (NodeResult result : results) {
            if (result.hasError())
                errors.add(result.getErrorList());
        }

        return Collections.unmodifiableList(errors);
    }

    public String getFlowId() {
        return flowId;
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "flowId='" + flowId + '\'' +
                ", results=" + results +
                '}';
    }
}
