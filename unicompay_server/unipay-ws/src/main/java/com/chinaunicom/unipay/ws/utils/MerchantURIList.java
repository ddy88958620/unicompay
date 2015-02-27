package com.chinaunicom.unipay.ws.utils;

public enum MerchantURIList {

	/**
	 * 商户直接退款接口地址
	 */
	DIRECT_REFUND_URI("/query_server/direct_refund"),
	
	/**
	 * 查询退款接口地址
	 */
	QUERY_REFUND("/query_server/refund_single"),
	
	/**
	 * 查询交易接口地址
	 */
	QUERY_PAY("/query_server/pay_single"),
	
	/**
	 * 获取消费清算对账单记录
	 */
	PAY_CLEAR_DATA("/query_server/pay_clear_data"),
	
	/**
	 * 获取退款清算对账单记录
	 */
	REFUND_CLEAR_DATA("/query_server/refund_clear_data"),
	;

	private String value;

	private MerchantURIList(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
