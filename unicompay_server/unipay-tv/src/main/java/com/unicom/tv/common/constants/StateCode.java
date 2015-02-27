package com.unicom.tv.common.constants;

public enum StateCode {

	COMPLETED(200),
	SERVER_ERROR(500),
	SERVICE_ERROR(501),
	PROTOCOL_ERROR(400),
	REQUEST_FORMAT_ERROR(10000),
	PARAM_NOT_COMPLATE(10001),
	SIGN_MSG_ERROR(10002),
	USERCODE_FORMAT_ERROR(10003),
	UPTIME_FORMAT_ERROR(10004),
	ACCESS_SIGN_ERROR(10005),
	MOBILE_BINDED(10006),
	TV_UNAVAILABLE(10007),
	IMEI_STATE_ERROR(10008),
	CODE_ERROR(10009),
	UNBIND_ERROR(10010);

	private int code;
	
	private StateCode(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}
