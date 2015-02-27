package com.unicom.tv.service.interfaces.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.unicom.tv.bean.interfaces.request.BindRequest;
import com.unicom.tv.bean.interfaces.request.PayRequest;
import com.unicom.tv.bean.interfaces.response.BindResponse;
import com.unicom.tv.bean.interfaces.response.PayResponse;
import com.unicom.tv.bean.pojo.TV;
import com.unicom.tv.bean.remote.request.TVPayRequest;
import com.unicom.tv.bean.remote.response.TVPayResponse;
import com.unicom.tv.common.constants.BizConstants;
import com.unicom.tv.common.constants.CacheConstants;
import com.unicom.tv.common.constants.RuntimeProperties;
import com.unicom.tv.common.constants.StateCode;
import com.unicom.tv.common.constants.RuntimeProperties.RuntimeKey;
import com.unicom.tv.common.utils.PatternUtil;
import com.unicom.tv.dao.TVDao;
import com.unicom.tv.exception.SysException;
import com.unicom.tv.service.CacheService;
import com.unicom.tv.service.RemotePayService;
import com.unicom.tv.service.interfaces.AccountService;
import com.unicom.tv.service.interfaces.PayService;

@Service
public class PayServiceImpl implements PayService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PayServiceImpl.class);
	
	@Resource
	private AccountService accountService;
	
	@Resource
	private CacheService cacheService;
	
	@Resource
	private RemotePayService remotePayService;
	
	@Resource
	private TVDao tvDao;
	
	@Override
	public PayResponse pay(PayRequest request) {
		LOGGER.debug("orderid:{},orderitem:{},cpid:{},appid{},channelid:{},serviceid:{},payfee:{},paytype:{},imei:{},appname:{},feename:{},appdeveloper:{},mac:{}", 
				request.getOrderId(), request.getOrderTime(), request.getCpId(),request.getAppId(), request.getChannelId(),
				request.getServiceId(), request.getPayFee(), request.getPayType(), request.getImei(),request.getAppName(),
				request.getFeeName(),request.getAppDeveloper(),request.getMacaddress());
		if (request.getOrderId() == null
				|| request.getOrderTime() == null
				|| request.getCpId() == null
				|| request.getAppId() == null
				|| request.getChannelId() == null
				|| request.getServiceId() == null
				|| request.getPayFee() == null
				|| request.getPayType() == null
				|| request.getImei() == null
				|| request.getAppName() == null
				|| request.getFeeName() == null
				|| request.getAppDeveloper() == null
				|| request.getMacaddress() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		
		TV tvQuery = new TV(request.getImei());
		TV tv = tvDao.getTV(tvQuery);

		PayResponse payResponse = null;
		if (request.getCode() == null) {
			
		//TODO: 校验应用密钥签名
//			if (!request.verifySign((String) cacheService.get(CacheConstants.PRIVATE_KEY + request.getImei()))) {
//				throw new SysException(StateCode.SIGN_MSG_ERROR);
//			}
			
			if (!request.verifyAccessKey((String) cacheService.get(CacheConstants.PRIVATE_KEY + request.getImei()))) {
				throw new SysException(StateCode.SIGN_MSG_ERROR);
			}
			
			payResponse = payDirect(request, tv);
		} else {
			payResponse =  payBindAndPay(request, tv);
		}
		return payResponse;
	}
	
	private PayResponse payBindAndPay(PayRequest request, TV tv) {
		if (request.getCode() == null || request.getUsercode() == null || request.getVersion() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}

		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		
		if (tv == null || tv.getMobilePhone() == null || tv.getAccessToken() == null) {
			throw new SysException(StateCode.IMEI_STATE_ERROR);
		}
		
		BindRequest bindRequest = new BindRequest();
		BeanUtils.copyProperties(request, bindRequest);
		BindResponse bindResponse = accountService.bind(bindRequest);
		
		PayResponse payResponse = payDirect(request, tvDao.getTV(new TV(request.getImei())));
		payResponse.setKey(bindResponse.getKey());
		
		return payResponse;
	}

	private PayResponse payDirect(PayRequest request, TV tv) {
		if (tv != null) {
			LOGGER.debug("mobile:{},code:{},token:{}", tv.getMobilePhone(), tv.getCode(), tv.getAccessToken());
		}
		
		if (tv == null || tv.getMobilePhone() == null || tv.getAccessToken() == null || tv.getCode() == null) {
			throw new SysException(StateCode.IMEI_STATE_ERROR);
		}

		TVPayRequest tvPayRequest = new TVPayRequest();
		BeanUtils.copyProperties(request, tvPayRequest);
		tvPayRequest.setUsercode(tv.getMobilePhone());
		tvPayRequest.setAccount(tv.getImei());
		tvPayRequest.setAccessToken(tv.getAccessToken());
		tvPayRequest.initSign(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY));
		
		TVPayResponse tvPayResponse = remotePayService.payTV(tvPayRequest);
		if (tvPayResponse == null || BizConstants.TV_PAY_HRET_FAIL.equals(tvPayResponse.gethRet())) {
			throw new SysException(StateCode.SERVICE_ERROR);
		}
		
		PayResponse payResponse = new PayResponse();
		payResponse.setOrderId(tvPayResponse.getOrderId());
		return payResponse;
	}

}
