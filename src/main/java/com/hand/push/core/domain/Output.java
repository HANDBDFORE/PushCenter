package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 2:48 PM
 */
public interface Output {
    public Output addErrorEntries(List<ErrorEntry> entries);
    public Output addErrorEntry(ErrorEntry entry);
    public List<ErrorEntry> getErrors();

    public Output addSuccessEntry(PushEntry entry);
    public Output addSuccessEntries(List<PushEntry> entries);
    public List<PushEntry> getSuccesses();

}
