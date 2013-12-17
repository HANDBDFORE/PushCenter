package com.hand.push.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 2:09 PM
 */
public class NodeResult {


    protected final List<ErrorEntry> errorEntries;

    public NodeResult() {
        errorEntries = new ArrayList<ErrorEntry>();
    }

    public static NodeResult success() {
        return new NodeResult();
    }

    public static NodeResult error(String message, Object source) {
        return new NodeResult().addError(message, source);

    }

    public NodeResult addError(String message, Object source) {
        errorEntries.add(new ErrorEntry(message, source));
        return this;
    }

    public NodeResult addErrors(List<ErrorEntry> errors) {
        if (errors != null && (!errors.isEmpty()))
            errorEntries.addAll(errors);
        return this;
    }

    public boolean hasError() {
        return errorEntries.size() != 0;
    }

    public List<ErrorEntry> getErrorList() {
        return Collections.unmodifiableList(errorEntries);
    }

    @Override
    public String toString() {
        return "NodeResult{" +
                "errorEntries=" + errorEntries +
                '}';
    }
}
