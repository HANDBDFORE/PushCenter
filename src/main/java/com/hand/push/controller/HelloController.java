package com.hand.push.controller;

import com.hand.push.core.App;
import com.hand.push.core.service.AppRegister;
import com.hand.push.dto.PushApp;
import com.hand.push.dto.PushRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HelloController {


    @Autowired
    private AppRegister regester;


    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        PushApp requestAppPacket = new PushApp("HR", "handhand");

        App app = regester.loadApp(requestAppPacket);

        System.out.println(app);
        model.addAttribute("message", "Hello world!");


        return "hello";
    }
}