package com.unicom.tv.bean.remote.request;

import java.text.MessageFormat;

import org.apache.commons.codec.digest.DigestUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.unicom.tv.security.Signal;
import com.unicom.tv.security.Verifiable;

@XStreamAlias("payReq")
public class TVPayRequest implements Verifiable, Signal {
	
	private static final String SIGN_PATTERN = "orderid={0}&usercode={1}&serviceid={2}&paytype={3}&channelid={4}&Key={5}";

	@XStreamAlias("orderid")
	private String orderId;
	
	@XStreamAlias("ordertime")
	private String orderTime;
	
	@XStreamAlias("cpid")
	private String cpId;
	
	@XStreamAlias("appid")
	private String appId;
	
	@XStreamAlias("channelid")
	private String channelId;
	
	@XStreamAlias("serviceid")
	private String serviceId;
	
	@XStreamAlias("payfee")
	private Integer payFee;
	
	@XStreamAlias("paytype")
	private Integer payType;
	
	@XStreamAlias("usercode")
	private String usercode;
	
	@XStreamAlias("imei")
	private String imei;
	
	@XStreamAlias("appname")
	private String appName;
	
	@XStreamAlias("feename")
	private String feeName;
	
	@XStreamAlias("account")
	private String account;
	
	@XStreamAlias("appdeveloper")
	private String appDeveloper;
	
	@XStreamAlias("macaddress")
	private String macaddress;
	
	@XStreamAlias("Accesstonken")
	private String accessToken;
	
	@XStreamAlias("signMsg")
	private String signMsg;

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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSignMsg() {
		return signMsg;
	}

	public void setSignMsg(String signMsg) {
		this.signMsg = signMsg;
	}

	@Override
	public boolean verifySign(String key) {
		return true;
	}

	@Override
	public String initSign(String key) {
		return this.signMsg = DigestUtils.md5Hex(MessageFormat.format(SIGN_PATTERN, orderId, usercode, serviceId, payType, channelId, key));
	}
}
