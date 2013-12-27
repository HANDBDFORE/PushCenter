package com.hand.push.controller;

import com.hand.push.core.ProcessorChain;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.dto.PushRequest;
import com.hand.push.util.JsonHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

import static com.hand.push.util.ResponseHelper.success;

@Controller
@RequestMapping("/push")
public class PortalController {

    @Resource(name = "processorChain")
    private ProcessorChain processorChain;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ?> printWelcome(@RequestParam("packet") String packetString) {

        PushRequest packet = build(packetString);
        getLogger().debug("Packet data is: " + packet.toString());

        String jobId = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis()));
        getLogger().info("jobId generated: " + jobId);

        MDC.put("jobId", jobId);

        Bundle bundle = new BundleImpl(packet, jobId);

        //处理
        processorChain.process(bundle);

        MDC.clear();
        return success("您的推送请求已经接受，请稍后根据jobId查询结果").addBody("jobId", jobId).result();


    }

    private PushRequest build(String packetString) throws IllegalArgumentException {

        if (StringUtils.isEmpty(packetString)) {
            getLogger().error("parameter[packet] is empty, request denied.");
            throw new IllegalArgumentException("消息不能为空");
        }


        return JsonHelper.stringToJson(packetString, PushRequest.class);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }


}