package com.hand.push.core;

import com.hand.push.core.domain.NodeResult;
import com.hand.push.dto.PushEntry;

import java.util.List;

/**
 * 推送器
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 10/14/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Pusher {
    /**
     * 告诉我，你能处理哪个平台
     * @return 平台名
     */
    public String tellMeYourDeviceType();

    /**
     * 推送数据
     * 注意，此方法<b>必须采用阻塞的方式进行推送</b>，在处理完数据后，如果有数据推送错误，请将数据加入到推送NodeResult进行返回。以便后续节点对数据进行处理
     * 如果需要并行推送以提高效率，建议采用 FutureTask管理
     *
     * @param pushRequests
     */
    public NodeResult push(List<PushEntry> pushRequests);
}
