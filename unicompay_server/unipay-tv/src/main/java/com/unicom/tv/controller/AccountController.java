package com.unicom.tv.controller;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.unicom.tv.bean.interfaces.request.CheckImeiRequest;
import com.unicom.tv.bean.interfaces.request.GetCodeRequest;
import com.unicom.tv.bean.interfaces.request.GetKeyRequest;
import com.unicom.tv.service.interfaces.AccountService;

@Controller
@RequestMapping("acc")
public class AccountController extends AbstractController{
	
	@Resource
	private AccountService accountService;
	
	@RequestMapping(value = "check_state", method = RequestMethod.POST)
	public ResponseEntity<String> checkState(@RequestBody String body) {
		return getResponse(accountService.getState(parseRequest(body, CheckImeiRequest.class)));
	}
	
	@RequestMapping(value = "get_key", method = RequestMethod.POST)
	public ResponseEntity<String> getKey(@RequestBody String body) {
		return getResponse(accountService.getKey(parseRequest(body, GetKeyRequest.class)));
	}
	
	@RequestMapping(value = "send_code", method = RequestMethod.POST)
	public ResponseEntity<String> getCode(@RequestBody String body) {
		return getResponse(accountService.getCode(parseRequest(body, GetCodeRequest.class)));
	}
}
