package com.unicom.tv.bean.pojo;

public class TV {

	private String imei;
	
	private String mobilePhone;
	
	private String mobileEncrypted;
	
	private String accessToken;
	
	private String code;
	
	public TV() {
	}
	
	public TV(String imei) {
		this.imei = imei;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getMobileEncrypted() {
		return mobileEncrypted;
	}

	public void setMobileEncrypted(String mobileEncrypted) {
		this.mobileEncrypted = mobileEncrypted;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
