package com.unicom.tv.service.interfaces.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicom.tv.bean.interfaces.request.BindRequest;
import com.unicom.tv.bean.interfaces.request.CheckImeiRequest;
import com.unicom.tv.bean.interfaces.request.GetCodeRequest;
import com.unicom.tv.bean.interfaces.request.GetKeyRequest;
import com.unicom.tv.bean.interfaces.request.UnbindRequest;
import com.unicom.tv.bean.interfaces.response.BindResponse;
import com.unicom.tv.bean.interfaces.response.CheckImeiResponse;
import com.unicom.tv.bean.interfaces.response.GetCodeResponse;
import com.unicom.tv.bean.interfaces.response.GetKeyResponse;
import com.unicom.tv.bean.interfaces.response.UnbindResponse;
import com.unicom.tv.bean.pojo.TV;
import com.unicom.tv.bean.remote.request.TVBindRequest;
import com.unicom.tv.bean.remote.request.TVCodeRequest;
import com.unicom.tv.bean.remote.request.TVUnbindRequest;
import com.unicom.tv.bean.remote.response.TVBindResponse;
import com.unicom.tv.bean.remote.response.TVCodeResponse;
import com.unicom.tv.bean.remote.response.TVUnbindResponse;
import com.unicom.tv.common.constants.BizConstants;
import com.unicom.tv.common.constants.CacheConstants;
import com.unicom.tv.common.constants.RuntimeProperties;
import com.unicom.tv.common.constants.RuntimeProperties.RuntimeKey;
import com.unicom.tv.common.constants.StateCode;
import com.unicom.tv.common.utils.DateUtil;
import com.unicom.tv.common.utils.PatternUtil;
import com.unicom.tv.dao.TVDao;
import com.unicom.tv.exception.SysException;
import com.unicom.tv.service.CacheService;
import com.unicom.tv.service.RemotePayService;
import com.unicom.tv.service.interfaces.AccountService;

@Service
public class AccountServiceImpl implements AccountService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Resource
	private TVDao tvDao;
	
	@Resource
	private CacheService cacheService;
	
	@Resource
	private RemotePayService remotePayService;

	@Override
	public CheckImeiResponse getState(CheckImeiRequest request) {
		if (request.getImei() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		
		TV tv = tvDao.getTV(new TV(request.getImei()));
		
		if (tv != null) {
			LOGGER.debug("mobile:{},code:{},token:{}", tv.getMobilePhone(), tv.getCode(), tv.getAccessToken());
		}
		
		String state = BizConstants.TV_STATE_NOT_REGISTER;
		String desc = null;
		
		if (tv != null) {
			String mobile = tv.getMobilePhone();
			String accessToken = tv.getAccessToken();
			String code = tv.getCode();
			
			if (code == null || accessToken == null) {
				state = BizConstants.TV_STATE_UBIND;
			}
			
			if (mobile != null && code != null && accessToken != null) {
				Object key = cacheService.get(CacheConstants.PRIVATE_KEY + tv.getImei());
				if (key == null) {
					state = BizConstants.TV_STATE_KEY_EXPIRED;
				} else {
					state = BizConstants.TV_STATE_KEY_AVAILABLE;
				}
			}
			desc = formatMobile(mobile);
		}
		
		CheckImeiResponse response = new CheckImeiResponse();
		response.setState(state);
		response.setDesc(desc);
		return response;
	}
	
	private String formatMobile(String mobile) {
		String formatedMobile = null;
		if (mobile != null && mobile.length() == 11) {
			formatedMobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
		}
		return formatedMobile;
	}

	@Override
	public GetCodeResponse getCode(GetCodeRequest request) {
		if (request.getImei() == null 
				|| request.getUsercode() == null
				|| request.getVersion() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		
		checkState(request);
		
		//下发验证码
		TVCodeRequest tvCodeRequest = new TVCodeRequest();
		BeanUtils.copyProperties(request, tvCodeRequest);
		tvCodeRequest.setUptime(DateUtil.format(new Date(), DateUtil.DATA_PATTERN_MINI));
		tvCodeRequest.initSign(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY));
		
		TVCodeResponse tvCodeResponse = remotePayService.getTVCode(tvCodeRequest);
		
		GetCodeResponse response = new GetCodeResponse();
		
		if (tvCodeResponse == null || BizConstants.TV_CODE_HRET_CODE_FAIL.equals(tvCodeResponse.gethRet())) {
			throw new SysException(StateCode.SERVICE_ERROR);
		} else if (BizConstants.TV_CODE_HRET_SUCCESS.equals(tvCodeResponse.gethRet())) {
			TV tvQuery = new TV(request.getImei());
			TV tv = tvDao.getTV(tvQuery);
			tvQuery.setAccessToken(tvCodeResponse.getAccessToken());
			tvQuery.setMobilePhone(request.getUsercode());
			
			if (tv == null) {
				tvDao.addTV(tvQuery);
			} else {
				tvDao.updateToGetCodeState(tvQuery);
			}
		}
		return response;
	}

	private void checkState(GetCodeRequest request) {
		//如果该imei已绑定手机号，先解绑，后下发验证码
//		TV tvQuery = new TV();
//		tvQuery.setMobilePhone(request.getUsercode());
		
//		TV tv = tvDao.getTV(tvQuery);
//		
//		if (tv != null && tv.getCode() != null && tv.getAccessToken() != null) {
//			UnbindRequest unbindRequest = new UnbindRequest();
//			unbindRequest.setImei(tv.getImei());
//			//解绑此手机号对应的绑定关系
//			this.unbind(unbindRequest);
//			
//			if (tv.getImei().equals(request.getImei())) return;
//		}
		
		UnbindRequest unbindRequest = new UnbindRequest();
		unbindRequest.setMobile(request.getUsercode());
		this.unbind(unbindRequest);
		
		TV tvQuery = new TV(request.getImei());
		TV tv = tvDao.getTV(tvQuery);

		if (tv != null && tv.getMobilePhone() != null && tv.getCode() != null && tv.getAccessToken() != null) {
			unbindRequest = new UnbindRequest();
			unbindRequest.setMobile(tv.getMobilePhone());
			//解绑此设备的绑定关系
			this.unbind(unbindRequest);
		}
	}

	@Override
	public GetKeyResponse getKey(GetKeyRequest request) {
		if (request.getImei() == null
				|| request.getCode() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		
		TV tv = tvDao.getTV(new TV(request.getImei()));
		
		if (tv == null) {
			throw new SysException(StateCode.TV_UNAVAILABLE);
		}
		
		String code = tv.getCode();
		if (code == null || tv.getAccessToken() == null || tv.getMobilePhone() == null) {
			throw new SysException(StateCode.IMEI_STATE_ERROR);
		}
		
		GetKeyResponse response = new GetKeyResponse();
		
		if (!request.getCode().equals(DigestUtils.md5Hex(tv.getCode()))) {
			throw new SysException(StateCode.CODE_ERROR);
		}
		response.setKey(cacheService.getNewSignKey(request.getImei()));
		return response;
	}
	
	@Override
	public BindResponse bind(BindRequest request) {
		if (request.getImei() == null
				|| request.getCode() == null
				|| request.getUsercode() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		if (!PatternUtil.match(request.getUsercode(), PatternUtil.MOBILE_PATTERN)) {
			throw new SysException(StateCode.USERCODE_FORMAT_ERROR);
		}
		
		TV tv = tvDao.getTV(new TV(request.getImei()));
		if (tv == null || tv.getMobilePhone() == null || tv.getAccessToken() == null 
				|| !tv.getMobilePhone().equals(request.getUsercode())) {
			throw new SysException(StateCode.IMEI_STATE_ERROR);
		}
		
		TVBindRequest tvBindRequest = new TVBindRequest();
		BeanUtils.copyProperties(request, tvBindRequest);
		tvBindRequest.setOrdercode(request.getCode());
		tvBindRequest.setAccessToken(tv.getAccessToken());
		tvBindRequest.setUpTime(DateUtil.format(new Date(), DateUtil.DATA_PATTERN_MINI));
		tvBindRequest.initSign(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY));
		
		BindResponse response = new BindResponse();
		TVBindResponse tvBindResponse = remotePayService.bindTV(tvBindRequest);
		if (tvBindResponse == null || BizConstants.TV_BIND_HRET_FAIL.equals(tvBindResponse.gethRet())) {
			throw new SysException(StateCode.SERVICE_ERROR);
		} else {
			tv.setCode(request.getCode());
			tvDao.saveBindRelationship(tv);
			response.setKey(cacheService.getNewSignKey(request.getImei()));
		}
		
		return response;
	}

	@Override
	@Transactional
	public UnbindResponse unbind(UnbindRequest request) {
		if (request.getMobile() == null && request.getImei() == null) {
			throw new SysException(StateCode.PARAM_NOT_COMPLATE);
		}
		
		TV tvQuery = new TV(request.getImei());
		
		if (request.getMobile() != null) {
			tvQuery.setMobilePhone(request.getMobile());
			TVUnbindRequest tvUnbindRequest = new TVUnbindRequest();
			tvUnbindRequest.setUsercode(request.getMobile());
			tvUnbindRequest.initSign(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY));
			TVUnbindResponse tvUnbindResponse = remotePayService.unbindTV(tvUnbindRequest);
//			if (tvUnbindResponse == null || BizConstants.TV_UNBIND_HRET_FAIL.equals(tvUnbindResponse.gethRet())) {
//				throw new SysException(StateCode.SERVICE_ERROR);
//			}
		} else {
			TV tv = tvDao.getTV(tvQuery);
			if (tv != null && tv.getMobilePhone() != null && tv.getCode() != null && tv.getAccessToken() != null) {
				TVUnbindRequest tvUnbindRequest = new TVUnbindRequest();
				tvUnbindRequest.setUsercode(tv.getMobilePhone());
				tvUnbindRequest.initSign(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY));
				TVUnbindResponse tvUnbindResponse = remotePayService.unbindTV(tvUnbindRequest);
				if (tvUnbindResponse == null || BizConstants.TV_UNBIND_HRET_FAIL.equals(tvUnbindResponse.gethRet())) {
					throw new SysException(StateCode.SERVICE_ERROR);
				}
			}
		}
		tvDao.unbindRelationship(tvQuery);
		return new UnbindResponse();
	}
	
}
