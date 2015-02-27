package com.chinaunicom.unipay.ws.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.unipay.ws.commons.HttpConnectionManager;
import com.chinaunicom.unipay.ws.commons.PropertiesConstant;
import com.chinaunicom.unipay.persistent.entities.ConsumecodeInfo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * 获取中兴计费平台 计费点信息
 * @author yangf
 * @date 2014-4-9
 */
public class ConsumeCodeUtils {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ConsumecodeInfo queryConsumeCodeInfo(String consumeCode) throws Exception{
		String url = PropertiesConstant.CONSUME_CODE_ADDR+consumeCode;
		HttpClient httpClient = new HttpClient(HttpConnectionManager.getConnectionManager());
		GetMethod get = new GetMethod(url);
		ConsumecodeInfo cci = null;
		try {
			//设置超时时间
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(6000);
			httpClient.getHttpConnectionManager().getParams().setIntParameter("http.socket.timeout", 6000);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(6000);
			
			httpClient.executeMethod(get);
			String responseBody = get.getResponseBodyAsString();
			
			logger.info("queryConsumeCodeInfo >>>> consumeCode="+consumeCode+";responseBody="+responseBody+"consumeCodeAddr="+url);
			
			if(responseBody.indexOf("{")!=-1){
				JSONObject json = JSON.parseObject(responseBody);
                cci = new ConsumecodeInfo();
				cci.setAppName((String)json.get("appname"));
				cci.setConsumeAmt(Integer.valueOf((String)json.get("fee")));
				cci.setConsumeCode(consumeCode);
				cci.setConsumeName((String)json.get("feename"));
				cci.setConsumeType(Integer.valueOf((String)json.get("feetype")));//（道具：1，关卡：2，按次代缴：3，包月：4）
				cci.setCpSecretKey((String)json.get("cpsecretkey"));
				cci.setMerUrl((String)json.get("callbackurl"));
				cci.setCreateTime(new Date());
			}
			return cci;
		} catch (HttpException e) {
			e.printStackTrace();
			return cci;
		} catch (IOException e) {
			e.printStackTrace();
			return cci;
		} finally {
			get.releaseConnection();
		}
	}
}
