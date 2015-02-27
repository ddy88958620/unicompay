package com.unicom.tv.bean.remote.request;

import java.text.MessageFormat;

import org.apache.commons.codec.digest.DigestUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.unicom.tv.security.Signal;
import com.unicom.tv.security.Verifiable;

@XStreamAlias("payReq")
public class TVUnbindRequest implements Verifiable, Signal {

	private static final String SIGN_PATTERN = "usercode={0}&Key={1}";
	
	@XStreamAlias("usercode")
	private String usercode;
	
	@XStreamAlias("signMsg")
	private String signMsg;

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
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
		return this.signMsg = DigestUtils.md5Hex(MessageFormat.format(SIGN_PATTERN, usercode, key));
	}
	
}
