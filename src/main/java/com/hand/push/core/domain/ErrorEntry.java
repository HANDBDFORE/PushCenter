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
public class ErrorEntry {
    private final List<PushEntry> data;
    private final Throwable causedBy;

    public ErrorEntry(Throwable causedBy, List<PushEntry> data) {
        this.causedBy = causedBy;
        this.data = new LinkedList<PushEntry>(data);

    }

    public ErrorEntry(Throwable causedBy, PushEntry data) {
        this(causedBy, Arrays.asList(data));

    }

    public List<PushEntry> getData() {
        return Collections.unmodifiableList(data);
    }

    public Throwable getCausedBy() {
        return causedBy;
    }

    @Override
    public String toString() {
        return "ErrorEntry{" +
                "causedBy=" + causedBy+
                ", data=" + data +
                '}';
    }
}
