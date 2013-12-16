package com.hand.push.core;

import com.hand.push.dto.PushPacket;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:07 PM
 */
public class BundleImpl implements Bundle{

    private PushPacket packet;

    @Override
    public PushPacket getPushPacket() {
        return packet;
    }
}
