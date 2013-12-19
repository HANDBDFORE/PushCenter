package com.hand.push.controller;

import com.hand.push.core.ProcessorChain;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.domain.ProcessResult;
import com.hand.push.dto.PushRequest;
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

@Controller
@RequestMapping("/push")
public class PortalController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "processorChain")
    private ProcessorChain processor;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String printWelcome(@RequestParam("packet") String packetString) {

        PushRequest packet = build(packetString);
        logger.debug("Packet data is: " + packet.toString());

        String jobId = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis()));
        logger.info("jobId generated: " + jobId);

        MDC.put("jobId", jobId);

        Bundle bundle = new BundleImpl(packet, jobId);

        ProcessResult re = processor.process(bundle);

        MDC.clear();

        return "ok";
    }

    private PushRequest build(String packetString) throws IllegalArgumentException {

        if (StringUtils.isEmpty(packetString)) {
            logger.error("parameter[packet] is empty, request denied.");
            throw new IllegalArgumentException("消息不能为空");
        }


        return JsonHelper.stringToJson(packetString, PushRequest.class);
    }


}