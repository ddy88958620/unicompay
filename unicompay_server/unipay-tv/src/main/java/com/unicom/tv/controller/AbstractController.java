package com.unicom.tv.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.unicom.tv.common.constants.StateCode;
import com.unicom.tv.common.utils.ResponseUtil;
import com.unicom.tv.exception.SysException;

public abstract class AbstractController {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		StateCode stateCode = StateCode.SERVER_ERROR;
		if (e instanceof SysException) {
			stateCode = ((SysException) e).getCode();
			if (stateCode == null) {
				stateCode = StateCode.SERVER_ERROR;
			}
		} else if (e instanceof JSONException) {
			stateCode = StateCode.REQUEST_FORMAT_ERROR;
		} else {
			e.printStackTrace();
		}
		return ResponseUtil.getReponse(stateCode.code(), null);
	}
	
	protected ResponseEntity<String> getResponse(Object response) {
		return ResponseUtil.getReponse(StateCode.COMPLETED.code(), response);
	}

    protected ResponseEntity<String> getOrginResponse(Object response) {
        return ResponseUtil.getOrginReponse(StateCode.COMPLETED.code(), response);
    }

	protected <T> T parseRequest(String body, Class<T> requestClass) {
		return JSON.parseObject(body, requestClass);
	}
}
