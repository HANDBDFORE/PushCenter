package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 2:54 PM
 */
public class OutputImpl implements Output{
    private final ConcurrentMap<PushEntry,Throwable> errors;
    private final Vector<PushEntry> success;



    public OutputImpl() {
        errors =new ConcurrentHashMap<PushEntry, Throwable>();
        success = new Vector<PushEntry>();
    }

    @Override
    public Output addErrorEntry(PushEntry entry,Throwable exception) {


        errors.putIfAbsent(entry,exception);
        return this;
    }

    @Override
    public Map<PushEntry,Throwable> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    @Override
    public Output addSuccessEntry(PushEntry entry) {
        success.add(entry);
        return this;
    }

    @Override
    public List<PushEntry> getSuccesses() {
        return Collections.unmodifiableList(success);
    }



    @Override
    public String toString() {
        return "OutputImpl{" +
                "errors=" + errors +
                ", success=" + success +
                '}';
    }

}

