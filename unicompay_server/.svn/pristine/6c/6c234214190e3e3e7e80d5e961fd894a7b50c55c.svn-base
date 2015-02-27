package com.unicom.tv.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.thoughtworks.xstream.XStream;
import com.unicom.tv.bean.remote.request.TVBindRequest;
import com.unicom.tv.bean.remote.request.TVCodeRequest;
import com.unicom.tv.bean.remote.request.TVPayRequest;
import com.unicom.tv.bean.remote.request.TVUnbindRequest;
import com.unicom.tv.bean.remote.response.TVBindResponse;
import com.unicom.tv.bean.remote.response.TVCodeResponse;
import com.unicom.tv.bean.remote.response.TVPayResponse;
import com.unicom.tv.bean.remote.response.TVUnbindResponse;
import com.unicom.tv.common.constants.RuntimeProperties;
import com.unicom.tv.common.constants.StateCode;
import com.unicom.tv.common.constants.XmlConstants;
import com.unicom.tv.common.constants.RuntimeProperties.RuntimeKey;
import com.unicom.tv.common.utils.DateUtil;
import com.unicom.tv.common.utils.PatternUtil;
import com.unicom.tv.exception.SysException;
import com.unicom.tv.service.RemotePayService;

@Service
public class RemotePayServiceImpl implements RemotePayService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("task");
	
	@Resource(name = "restTemplate")
	private RestTemplate restTemplate;
	
	private static final XStream TV_CODE = new XStream();
	private static final XStream TV_BIND = new XStream();
	private static final XStream TV_UNBIND = new XStream();
	private static final XStream TV_PAY = new XStream();
	
	static {
		TV_CODE.processAnnotations(new Class[]{TVCodeRequest.class, TVCodeResponse.class});
		TV_BIND.processAnnotations(new Class[]{TVBindRequest.class, TVBindResponse.class});
		TV_UNBIND.processAnnotations(new Class[]{TVUnbindRequest.class, TVUnbindResponse.class});
		TV_PAY.processAnnotations(new Class[]{TVPayRequest.class, TVPayResponse.class});
	}
	
	private static final MultiValueMap<String, String> HEADERS;
	
	static {
		HEADERS = new LinkedMultiValueMap<String, String>();
		HEADERS.set("Content-Type", "application/xml;charset=utf-8");
		HEADERS.set("Accept", "application/xml");
	}

	@Override
	public TVCodeResponse getTVCode(TVCodeRequest request) {
		if (request.getUsercode() == null
				|| request.getImei() == null
				|| request.getUptime() == null
				|| request.getVersion() == null
				|| request.getSignMsg() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		if (!DateUtil.isDateType(request.getUptime(), DateUtil.DATA_PATTERN_MINI)) {
			throw new SysException(StateCode.UPTIME_FORMAT_ERROR);
		}
//		if (!request.verifySign((String) cacheService.get(CacheConstants.SYSTEM_PAY_KEY))) {
		if (!request.verifySign(null)) {
			throw new SysException(StateCode.SIGN_MSG_ERROR);
		}
		
		String body = XmlConstants.XML_HEADER + TV_CODE.toXML(request);
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, HEADERS);
		ResponseEntity<String> entity = restTemplate.exchange(RuntimeProperties.get(RuntimeKey.PAY_TV_CODE), HttpMethod.POST, httpEntity, String.class);
		
		if (entity.hasBody()) {
			LOGGER.info("请求下发验证码：\n{}\n请求状态：\n{}\n结果：\n{}", body, entity.getStatusCode().value(), entity.getBody());
		}
		
		if (entity.getStatusCode().is2xxSuccessful() && entity.hasBody()) {
			TVCodeResponse response = (TVCodeResponse) TV_CODE.fromXML(entity.getBody());
			return response;
		}
		return null;
	}

	@Override
	public TVBindResponse bindTV(TVBindRequest request) {
		if (request.getOrdercode() == null
				|| request.getImei() == null
				|| request.getUsercode() == null
				|| request.getAccessToken() == null
				|| request.getUpTime() == null
				|| request.getVersion() == null
				|| request.getSignMsg() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		if (!DateUtil.isDateType(request.getUpTime(), DateUtil.DATA_PATTERN_MINI)) {
			throw new SysException(StateCode.UPTIME_FORMAT_ERROR);
		}
//		if (!request.verifySign((String) cacheService.get(CacheConstants.SYSTEM_PAY_KEY))) {
		if (!request.verifySign(null)) {
			throw new SysException(StateCode.SIGN_MSG_ERROR);
		}
		
		String body = XmlConstants.XML_HEADER + TV_BIND.toXML(request);
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, HEADERS);
		ResponseEntity<String> entity = restTemplate.exchange(RuntimeProperties.get(RuntimeKey.PAY_TV_BIND), HttpMethod.POST, httpEntity, String.class);
		
		if (entity.hasBody()) {
			LOGGER.info("请求绑定：\n{}\n请求状态：\n{}\n结果：\n{}", body, entity.getStatusCode().value(), entity.getBody());
		}
		
		if (entity.getStatusCode().is2xxSuccessful() && entity.hasBody()) {
			TVBindResponse response = (TVBindResponse) TV_BIND.fromXML(entity.getBody());
			return response;
		}
		return null;
	}

	@Override
	public TVUnbindResponse unbindTV(TVUnbindRequest request) {
		if (request.getUsercode() == null
				|| request.getSignMsg() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
//		if (!request.verifySign((String) cacheService.get(CacheConstants.SYSTEM_PAY_KEY))) {
		if (!request.verifySign(null)) {
			throw new SysException(StateCode.SIGN_MSG_ERROR);
		}
		
		String body = XmlConstants.XML_HEADER + TV_UNBIND.toXML(request);
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, HEADERS);
		ResponseEntity<String> entity = restTemplate.exchange(RuntimeProperties.get(RuntimeKey.PAY_TV_UNBIND), HttpMethod.POST, httpEntity, String.class);
		
		if (entity.hasBody()) {
			LOGGER.info("请求去解绑：\n{}\n请求状态：\n{}\n结果：\n{}", body, entity.getStatusCode().value(), entity.getBody());
		}
		
		if (entity.getStatusCode().is2xxSuccessful() && entity.hasBody()) {
			TVUnbindResponse response = (TVUnbindResponse) TV_UNBIND.fromXML(entity.getBody());
			return response;
		}
		return null;
	}

	@Override
	public TVPayResponse payTV(TVPayRequest request) {
		if (request.getOrderId() == null
				|| request.getOrderTime() == null
				|| request.getCpId() == null
				|| request.getAppId() == null
				|| request.getChannelId() == null
				|| request.getServiceId() == null
				|| request.getPayFee() == null
				|| request.getPayType() == null
				|| request.getUsercode() == null
				|| request.getImei() == null
				|| request.getAppName() == null
				|| request.getFeeName() == null
				|| request.getAccount() == null
				|| request.getAppDeveloper() == null
				|| request.getMacaddress() == null
				|| request.getAccessToken() == null
				|| request.getSignMsg() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		if (!DateUtil.isDateType(request.getOrderTime(), DateUtil.DATA_PATTERN_MINI)) {
			throw new SysException(StateCode.UPTIME_FORMAT_ERROR);
		}
//		if (!request.verifySign((String) cacheService.get(CacheConstants.SYSTEM_PAY_KEY))) {
		if (!request.verifySign(null)) {
			throw new SysException(StateCode.SIGN_MSG_ERROR);
		}

		String body = XmlConstants.XML_HEADER + TV_PAY.toXML(request);
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, HEADERS);
		ResponseEntity<String> entity = restTemplate.exchange(RuntimeProperties.get(RuntimeKey.PAY_TV_PAY), HttpMethod.POST, httpEntity, String.class);
		
		if (entity.hasBody()) {
			LOGGER.info("请求支付：\n{}\n请求状态：\n{}\n结果：\n{}", body, entity.getStatusCode().value(), entity.getBody());
		}
		
		if (entity.getStatusCode().is2xxSuccessful() && entity.hasBody()) {
			TVPayResponse response = (TVPayResponse) TV_PAY.fromXML(entity.getBody());
			return response;
		}
		
		
		return null;
	}

}
