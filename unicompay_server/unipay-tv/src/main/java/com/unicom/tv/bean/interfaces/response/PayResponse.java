package com.unicom.tv.bean.interfaces.response;

import com.alibaba.fastjson.annotation.JSONField;

public class PayResponse {

	@JSONField(name = "order_id")
	private String orderId;
	
	@JSONField(name = "key")
	private String key;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
