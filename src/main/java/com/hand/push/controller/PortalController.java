package com.hand.push.controller;

import com.hand.push.core.Pusher;
import com.hand.push.core.service.AppRegester;
import com.hand.push.dto.PushPacket;
import com.hand.push.impl.repository.AppRegesterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/push")
public class PortalController {





	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	public String printWelcome(@RequestParam("packet") PushPacket packet) {






		 return "ok";
	}





}