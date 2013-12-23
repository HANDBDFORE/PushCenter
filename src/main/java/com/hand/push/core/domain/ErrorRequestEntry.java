package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 2:27 PM
 */
public class ErrorRequestEntry extends ErrorEntry {
    private final List<PushEntry> data;

    public ErrorRequestEntry(Throwable causedBy, List<PushEntry> data) {
        super(causedBy);
        this.data = new LinkedList<PushEntry>(data) ;

    }

    public ErrorRequestEntry(Throwable causedBy, PushEntry data) {
       this(causedBy, Arrays.asList(data));

    }

    public List<PushEntry> getData() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public String toString() {
        return "ErrorRequestEntry{" +
                "causedBy=" + getCausedBy() +
                ", data=" + data +
                '}';
    }
}
