package com.hand.push.core.domain;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 11:26 AM
 */
public class ErrorEntry {
    private final Throwable causedBy;

    public ErrorEntry(Throwable causedBy) {
        this.causedBy = causedBy;
    }

    public Throwable getCausedBy() {
        return causedBy;
    }
}
