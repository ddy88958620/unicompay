/*
 * Copyright  2013，the original authors or Tianyuan DIC Computer Co., Ltd.
 *
 * Please see the tydic success stories and it solutions 
 *
 *      http://www.tydic.com/Default.aspx
 *
 * Statement: This document's code after sufficiently has not permitted does not have 
 * any way dissemination and the change, once discovered violates the stipulation, will
 * receive the criminal sanction.
 * Address: 3/F,T3 Building, South 7th Road, South Area, Hi-tech Industrial park, Shenzhen, P.R.C.
 * Email: webmaster@tydic.com　
 * Tel: +86 755 26745688 
 */
package com.unicom.order.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.tydic.common.bean.http.ServiceInfLogBean;
import com.tydic.common.constant.code.ErrorCodeConstants;
import com.tydic.common.exception.InterfaceServiceException;
import com.tydic.common.interfaces.context.InterfaceContext;
import com.tydic.common.interfaces.context.InterfaceManager;
import com.tydic.common.interfaces.interceptors.plugins.AbstractPlugin;
import com.tydic.common.interfaces.interceptors.plugins.InterceptorPlugin;
import com.unicom.order.util.OrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * //TODO
 * 
 * @author zhouxw {@link zhouxw@tydic.com}
 * @version Loyalty 上午11:15:29
 * @since 1.0
 **/
public class OrderLogInterceptorPlugin extends AbstractPlugin implements InterceptorPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("record");

	@Override
	public boolean before(HttpServletRequest request) {

		return true;
	}

	@Override
	public boolean after(HttpServletRequest request) {
		InterfaceContext context = InterfaceManager.getInterfaceContext();
		ServiceInfLogBean logBan = new ServiceInfLogBean();
		try {
			logBan.setServiceUrl(context.getRequest().getRequestURI());
			logBan.setMsgId(context.getMsgId());
			logBan.setStas(context.getStas());
			logBan.setErrNote(context.getErrorMsg());
			logBan.setClientId(context.getClientId());
			logBan.setReqTime(context.getCrtTime());
			logBan.setResTime(new Date());
			logBan.setUserId(context.getUserId());
			logBan.setUserIp(OrderUtil.getRequestIP(context.getRequest()));
			logBan.setReqHead(context.getReqHeadString());
			logBan.setResHead(context.getResHeadToString());
			logBan.setReqBody(context.getReqBody());
			logBan.setResBody(context.getResBody());

            LOGGER.info(logBan.getServiceUrl() + " " + JSONObject.toJSONString(logBan));

		} catch (Exception e) {
			throw new InterfaceServiceException("系统错误", e, ErrorCodeConstants.SYSTEM_ERROR_CODE);
		}
		return true;
	}

}
