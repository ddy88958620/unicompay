package com.unicom.tv.common.constants;

public interface BizConstants {

	/*
	 * imei对应的状态
	 * 1000： 未注册盒子 （走绑定流程）
	 * 1001： 解除绑定盒子 （走绑定流程）
	 * 1002： 已绑定，授权过期 （在支付之前需要走重新授权流程）
	 * 1111： 已绑定，授权有效 （可以直接支付）
	 */
	static final String TV_STATE_NOT_REGISTER = "1000";
	static final String TV_STATE_UBIND = "1001";
	static final String TV_STATE_KEY_EXPIRED = "1002";
	static final String TV_STATE_KEY_AVAILABLE = "1111";
	
	static final String TV_CODE_HRET_SUCCESS = "0";
	static final String TV_CODE_HRET_CODE_FAIL = "1";
	
	static final String TV_BIND_HRET_FAIL = "1";
	static final String TV_BIND_HRET_SUCCESS = "0";
	static final String TV_UNBIND_HRET_FAIL = "1";
	static final String TV_UNBIND_HRET_SUCCESS = "0";
	static final String TV_PAY_HRET_FAIL = "1";
	static final String TV_PAY_HRET_SUCCESS = "0";
	
}
