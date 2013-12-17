package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.NodeResult;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:04 PM
 */
public interface Processor {

    public NodeResult process(Bundle bundle);
}
