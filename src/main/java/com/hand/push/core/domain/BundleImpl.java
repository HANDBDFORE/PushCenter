package com.hand.push.core.domain;

import com.hand.push.dto.PushRequest;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:07 PM
 */
public class BundleImpl implements Bundle {

    private final PushRequest packet;
    private final String jobId;

    public BundleImpl(PushRequest packet, String jobId) {


        this.jobId = jobId;
        this.packet = packet;
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
    public String toString() {
        return "BundleImpl{" +
                "packet=" + packet +
                ", jobId='" + jobId + '\'' +
                '}';
    }
}
