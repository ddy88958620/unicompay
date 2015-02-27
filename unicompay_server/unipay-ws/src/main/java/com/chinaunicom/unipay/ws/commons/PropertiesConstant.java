package com.chinaunicom.unipay.ws.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class PropertiesConstant {
	static Logger logger = LoggerFactory.getLogger(PropertiesConstant.class);

	public static final String POINT_CONSUME_ADDR;
	public static final String CONSUME_CODE_ADDR;
	
	public static final String POINT_RATE;
	
	public static final String SP_NO;
	public static final String BFB_PAY_DIRECT_NO_LOGIN_URL;
	public static final String BFB_PAY_DIRECT_LOGIN_URL;
	public static final String BFB_PAY_WAP_DIRECT_URL;
	public static final String BFB_QUERY_ORDER_URL;
	public static final String BFB_INTERFACE_ENCODING;
	public static final String SIGN_METHOD_MD5;
	public static final String IGN_METHOD_SHA1;
	public static final String BFB_PAY_INTERFACE_SERVICE_ID;
	public static final String BFB_QUERY_INTERFA;
	public static final String BFB_INTERFACE_VERSION;
	public static final String BFB_INTERFACE_OUTPUT_FORMAT;
	public static final String BFB_QUERY_INTERFACE_SERVICE_ID;
	public static final String BFB_INTERFACE_CURRENTCY;
	public static final String SP_PAY_RESULT_SUCCESS;
	public static final String SP_PAY_RESULT_WAITING;
	public static final String BFB_KEY;
	
	public static final String BFB_PAGE_URL;
	public static final String BFB_RETURN_URL;
	
	static {
		logger.debug(">>>>>>>>开始读取[common_config.properties]配置文件................................");
		ResourceBundle bundle = ResourceBundle.getBundle("common_config");
		
		POINT_CONSUME_ADDR = bundle.getString("point_consume_addr");
		CONSUME_CODE_ADDR = bundle.getString("consume_code_addr");
		
		POINT_RATE = bundle.getString("point_rate");
		
		SP_NO = bundle.getString("sp_no");
		BFB_PAY_DIRECT_NO_LOGIN_URL = bundle.getString("bfb_pay_direct_no_login_url");
		BFB_PAY_DIRECT_LOGIN_URL = bundle.getString("bfb_pay_direct_login_url");
		BFB_PAY_WAP_DIRECT_URL = bundle.getString("bfb_pay_wap_direct_url");
		BFB_QUERY_ORDER_URL = bundle.getString("bfb_query_order_url");
		BFB_INTERFACE_ENCODING = bundle.getString("bfb_interface_encoding");
		SIGN_METHOD_MD5 = bundle.getString("sign_method_md5");
		IGN_METHOD_SHA1 = bundle.getString("ign_method_sha1");
		BFB_PAY_INTERFACE_SERVICE_ID = bundle.getString("bfb_pay_interface_service_id");
		BFB_QUERY_INTERFA = bundle.getString("bfb_query_interfa");
		BFB_INTERFACE_VERSION = bundle.getString("bfb_interface_version");
		BFB_INTERFACE_OUTPUT_FORMAT = bundle.getString("bfb_interface_output_format");
		BFB_QUERY_INTERFACE_SERVICE_ID = bundle.getString("bfb_query_interface_service_id");
		BFB_INTERFACE_CURRENTCY = bundle.getString("bfb_interface_currentcy");
		SP_PAY_RESULT_SUCCESS = bundle.getString("sp_pay_result_success");
		SP_PAY_RESULT_WAITING = bundle.getString("sp_pay_result_waiting");
		 
		BFB_KEY = bundle.getString("bfb_key");
		
		BFB_PAGE_URL = bundle.getString("bfb_page_url");
		BFB_RETURN_URL = bundle.getString("bfb_return_url");
		
		logger.debug(">>>>>>>>成功读取[common_config.properties]配置文件................................");
	}
}