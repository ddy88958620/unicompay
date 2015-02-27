package com.unicom.tv.controller;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.unicom.tv.bean.interfaces.request.PayRequest;
import com.unicom.tv.service.interfaces.PayService;

@Controller
@RequestMapping("pay")
public class PayController extends AbstractController {
	
	@Resource
	private PayService payService;
	
	@RequestMapping(value = "d_pay", method = RequestMethod.POST)
	public ResponseEntity<String> pay(@RequestBody String body) {
		System.out.println(body);
		return getResponse(payService.pay(parseRequest(body, PayRequest.class)));
	}
}
