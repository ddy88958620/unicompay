package com.unicom.tv.bean.remote.request;

import java.text.MessageFormat;

import org.apache.commons.codec.digest.DigestUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.unicom.tv.security.Signal;
import com.unicom.tv.security.Verifiable;


@XStreamAlias("payReq")
public class TVCodeRequest implements Verifiable, Signal {
	
	private static final String SIGN_PATTERN = "usercode={0}&imei={1}&Key={2}";

	@XStreamAlias("usercode")
	private String usercode;
	
	@XStreamAlias("imei")
	private String imei;
	
	@XStreamAlias("Version")
	private String version;
	
	@XStreamAlias("Uptime")
	private String uptime;
	
	@XStreamAlias("signMsg")
	private String signMsg;
	
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
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
		return this.signMsg = DigestUtils.md5Hex(MessageFormat.format(SIGN_PATTERN, usercode, imei, key));
	}
	
}
