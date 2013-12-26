package com.hand.push.core.domain;

import com.hand.push.core.dto.PushEntry;
import com.hand.push.core.dto.PushRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:07 PM
 */
public class BundleImpl implements Bundle {

    private final PushRequest packet;
    private final String jobId;
    private final Output output;
    private final long timestamp;

    public BundleImpl(PushRequest packet, String jobId) {
        check(packet, jobId);

        this.jobId = jobId;
        this.packet = packet;
        output = new OutputImpl();
        this.timestamp = System.currentTimeMillis();
    }

    private void check(PushRequest packet, String jobId) throws IllegalArgumentException {
        if (packet == null) throw new IllegalArgumentException("packet 不能为空");
        if (jobId == null || jobId.trim().length() == 0) throw new IllegalArgumentException("jobId 不能为空");
    }

    @Override
    public PushRequest getPushPacket() {
        return packet;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public Output getOutput() {
        return output;
    }


    @Override
    public List<PushEntry> getUnProcessedEntries() {
        List<PushEntry> processedList = new LinkedList<PushEntry>();

        //添加处理成功的项
        processedList.addAll(output.getSuccesses());

        //添加失败项
        processedList.addAll(output.getErrors().keySet());

        List<PushEntry> rawCopy = new LinkedList<PushEntry>(packet.getEntries());
        rawCopy.removeAll(processedList);

        return rawCopy;
    }

    @Override
    public long createDate() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "BundleImpl{" +
                "packet=" + packet +
                ", jobId='" + jobId + '\'' +
                '}';
    }
}
