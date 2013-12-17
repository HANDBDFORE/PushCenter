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

    public BundleImpl(PushRequest packet) {
        this.packet = packet;
    }

    @Override
    public PushRequest getPushPacket() {
        return packet;
    }

    @Override
    public String toString() {
        return "BundleImpl{" +
                "packet=" + packet +
                '}';
    }
}
