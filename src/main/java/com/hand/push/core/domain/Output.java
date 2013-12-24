package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 2:48 PM
 */
public interface Output {
    public Output addErrorEntry(PushEntry entry,Throwable exception);
    public Map<PushEntry,Throwable> getErrors();

    public Output addSuccessEntry(PushEntry entry);
    public List<PushEntry> getSuccesses();

}
