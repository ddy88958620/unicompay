package com.unicom.tv.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSON;

public class ResponseUtil {
	
	public static final Object EMPTY_OBJ = new Object();
	
	public static ResponseEntity<String> getReponse(int state, Object body) {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("state_code", state + "");
		content.put("result", body == null ? EMPTY_OBJ : body);
		return new ResponseEntity<String>(JSON.toJSONString(content), buildGlobalHeaders(), HttpStatus.OK);
	}

    public static ResponseEntity<String> getOrginReponse(int state, Object body) {
		return new ResponseEntity<String>(JSON.toJSONString(body), buildGlobalHeaders(), HttpStatus.OK);
	}
	
	private static HttpHeaders buildGlobalHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json;charset=UTF-8");
		return headers;
	}
}
