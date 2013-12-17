package com.hand.push.core;

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

    private final List<NodeResult> results;

    private ProcessResult(List<NodeResult> results) {
        this.results = results;
    }

    public static ProcessResult construct() {
        return new ProcessResult(new ArrayList<NodeResult>());
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


    @Override
    public String toString() {
        return "ProcessResult{" +
                "results=" + results +
                '}';
    }
}
