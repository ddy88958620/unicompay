package com.unicom.tv.bean.interfaces.request;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.unicom.tv.security.Verifiable;

public class PayRequest implements Verifiable {
	
	@JSONField(name = "order_id")
	private String orderId;
	
	@JSONField(name = "ordertime")
	private String orderTime;
	
	@JSONField(name = "cpid")
	private String cpId;
	
	@JSONField(name = "appid")
	private String appId;
	
	@JSONField(name = "channel_id")
	private String channelId;
	
	@JSONField(name = "service_id")
	private String serviceId;
	
	@JSONField(name = "payfee")
	private Integer payFee;
	
	@JSONField(name = "paytype")
	private Integer payType;
	
	@JSONField(name = "usercode")
	private String usercode;
	
	@JSONField(name = "imei")
	private String imei;
	
	@JSONField(name = "app_name")
	private String appName;
	
	@JSONField(name = "fee_name")
	private String feeName;
	
	@JSONField(name = "app_developer")
	private String appDeveloper;
	
	@JSONField(name = "mac_address")
	private String macaddress;
	
	@JSONField(name = "sign_msg")
	private String signMsg;
	
	@JSONField(name = "code")
	private String code;
	
	private String version;
	
	@JSONField(name = "sign")
	private String sign;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getPayFee() {
		return payFee;
	}

	public void setPayFee(Integer payFee) {
		this.payFee = payFee;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public String getAppDeveloper() {
		return appDeveloper;
	}

	public void setAppDeveloper(String appDeveloper) {
		this.appDeveloper = appDeveloper;
	}

	public String getMacaddress() {
		return macaddress;
	}

	public void setMacaddress(String macaddress) {
		this.macaddress = macaddress;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getVersion() {
		
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	@Override
	public boolean verifySign(String key) {
		if (signMsg != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("order_id=").append(orderId)
				.append("&service_id=").append(serviceId)
				.append("&paytype=").append(payType)
				.append("&channel_id=").append(channelId)
				.append("&ordertime=").append(orderTime)
				.append("&key=").append(key);
			
			if (DigestUtils.md5Hex(builder.toString()).equals(signMsg)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean verifyAccessKey(String key) {
		if (sign != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("order_id=").append(orderId)
				.append("&service_id=").append(serviceId)
				.append("&paytype=").append(payType)
				.append("&channel_id=").append(channelId)
				.append("&ordertime=").append(orderTime)
				.append("&key=").append(key);
			return DigestUtils.md5Hex(builder.toString()).equals(sign);
		}
		
		return false;
	}
}
