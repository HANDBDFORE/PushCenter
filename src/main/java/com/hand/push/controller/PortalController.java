package com.hand.push.controller;

import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.BundleImpl;
import com.hand.push.core.ProcessorChain;
import com.hand.push.dto.PushRequest;
import com.hand.push.util.JsonHelper;
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

        Bundle bundle = new BundleImpl(build(packetString));
        processor.process(bundle);

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