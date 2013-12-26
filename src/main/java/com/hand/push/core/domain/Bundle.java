package com.hand.push.core.domain;

import com.hand.push.core.dto.PushEntry;
import com.hand.push.core.dto.PushRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:05 PM
 */
public interface Bundle {

    public PushRequest getPushPacket();

    public String getJobId();

    public Output getOutput();

    public List<PushEntry> getUnProcessedEntries();

    public long createDate();
}
