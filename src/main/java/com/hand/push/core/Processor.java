package com.hand.push.core;

import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.NodeResult;

/**
 * 处理节点
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 5:04 PM
 */
public interface Processor {

    /**处理传入的数据
     *
     * <b>注意：实现此接口时，请尽量将执行过程中产生的异常包装为直接结果进行返回，以便系统进行错误信息收集</b>
     * @param bundle 数据包
     * @return 该节点处理结果
     */
    public NodeResult process(Bundle bundle);
}
