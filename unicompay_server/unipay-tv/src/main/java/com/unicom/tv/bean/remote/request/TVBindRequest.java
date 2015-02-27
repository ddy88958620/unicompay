package com.unicom.tv.bean.remote.request;

import java.text.MessageFormat;

import org.apache.commons.codec.digest.DigestUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.unicom.tv.security.Signal;
import com.unicom.tv.security.Verifiable;

@XStreamAlias("payReq")
public class TVBindRequest implements Verifiable, Signal {
	
	private static final String SIGN_PATTERN = "ordercode={0}&usercode={1}&imei={2}&Key={3}";

	@XStreamAlias("ordercode")
	private String ordercode;
	
	@XStreamAlias("usercode")
	private String usercode;
	
	@XStreamAlias("imei")
	private String imei;
	
	@XStreamAlias("Accesstonken")
	private String accessToken;
	
	@XStreamAlias("Version")
	private String version;
	
	@XStreamAlias("Uptime")
	private String upTime;
	
	@XStreamAlias("signMsg")
	private String signMsg;

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
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
		return this.signMsg = DigestUtils.md5Hex(MessageFormat.format(SIGN_PATTERN, ordercode, usercode, imei, key));
	}
	
}
