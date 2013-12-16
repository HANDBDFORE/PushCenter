package com.hand.push.controller;

import com.hand.push.core.ComponentFinder;
import com.hand.push.core.service.AppRegester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HelloController {


    @Autowired
    private AppRegester regester;

    @Autowired
    private ComponentFinder finder;


    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        regester.load("aerwr", "afasdfasdr");
        model.addAttribute("message", "Hello world!");


        return "hello";
    }
}