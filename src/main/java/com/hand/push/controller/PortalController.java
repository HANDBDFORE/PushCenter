package com.hand.push.controller;

import com.hand.push.core.LogUtil;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.ProcessorChain;
import com.hand.push.core.domain.ProcessResult;
import com.hand.push.dto.PushRequest;
import com.hand.push.util.JsonHelper;
import org.apache.commons.codec.digest.DigestUtils;
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

    @Resource(name = "processorChain")
    private ProcessorChain processor;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String printWelcome(@RequestParam("packet") String packetString) {

        String jobId = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis()));
        MDC.put("jobId", jobId);



        Bundle bundle = new BundleImpl(build(packetString), jobId);
        ProcessResult re = processor.process(bundle);
        System.out.println(re.getFlowId());

        MDC.clear();

        return "ok";
    }

    private PushRequest build(String packetString) {

        System.out.println(packetString);


        if (StringUtils.isEmpty(packetString)) {
            //TODO 记录日志
            throw new IllegalArgumentException("消息不能为空");
        }



        return JsonHelper.stringToJson(packetString, PushRequest.class);
    }


}