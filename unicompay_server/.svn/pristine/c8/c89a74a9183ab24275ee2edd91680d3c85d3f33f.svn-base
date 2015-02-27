package com.unicom.sms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.service.sgip.SgipSession;
import com.unicom.sms.util.JSONUtils;
import com.unicom.sms.util.SMSContants;
import com.unicom.sms.util.SpringContextUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaofrancis on 15/1/19.
 */
public class SMSService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSService.class);

    private Map<String, String> sgipParamMap = new HashMap<String, String>();
    private static JSONObject configJson = null;

    private static Map<String, Map<String, String>> spInfoMaps = new HashMap<String, Map<String, String>>();

    static {
        try {
            InputStream inputStream = SMSService.class.getClassLoader().getResourceAsStream("config.json");
            configJson = JSONUtils.parseJSONFile(inputStream);
            JSONArray spArray = configJson.getJSONArray(SMSContants.PARAM_SP_INFO);
            for (int i = 0; i < spArray.size(); i++) {
                JSONObject spObject = spArray.getJSONObject(i);
                String account = spObject.getString(SMSContants.PARAM_LOCAL_ACCOUNT);
                String password = spObject.getString(SMSContants.PARAM_LOCAL_PASSWORD);
                String port = spObject.getString(SMSContants.PARAM_LOCAL_PORT);
                String handlerUrl = spObject.getString(SMSContants.PARAM_HANDLER_URL);
                String spNumber = spObject.getString(SMSContants.PARAM_SP_NUMBER);
                String remoteAccount = spObject.getString(SMSContants.PARAM_REMOTE_ACCOUNT);
                String remotePassword = spObject.getString(SMSContants.PARAM_REMOTE_PASSWORD);
                String remoteIP = spObject.getString(SMSContants.PARAM_REMOTE_IP);
                String remotePort = spObject.getString(SMSContants.PARAM_REMOTE_PORT);

                if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(handlerUrl) || StringUtils.isBlank(port)) {
                    throw new NullPointerException();
                }

                Map<String, String> spInfoMap = new HashMap<String, String>();
                spInfoMap.put(SMSContants.PARAM_LOCAL_ACCOUNT, account);
                spInfoMap.put(SMSContants.PARAM_LOCAL_PASSWORD, password);
                spInfoMap.put(SMSContants.PARAM_LOCAL_PORT, port);
                spInfoMap.put(SMSContants.PARAM_HANDLER_URL, handlerUrl);
                spInfoMap.put(SMSContants.PARAM_REMOTE_ACCOUNT, remoteAccount);
                spInfoMap.put(SMSContants.PARAM_REMOTE_PASSWORD, remotePassword);
                spInfoMap.put(SMSContants.PARAM_REMOTE_IP, remoteIP);
                spInfoMap.put(SMSContants.PARAM_REMOTE_PORT, remotePort);

                spInfoMaps.put(spNumber, spInfoMap);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void init() {
        this.startListen();
    }

    private void startListen() {
        try {
            if (null != spInfoMaps && spInfoMaps.size() != 0) {
                for (String spNumber : spInfoMaps.keySet()) {
                    SMSCallBackService callBackService = (SMSCallBackService) SpringContextUtils.getInstance().getApplicationContext().getBean("SMSCallBackService");
                    callBackService.setHandlerUrl(spInfoMaps.get(spNumber).get("handler_url"));
                    new SgipSession(spInfoMaps.get(spNumber).get("local_account"), spInfoMaps.get(spNumber).get("local_password"), Integer.valueOf(spInfoMaps.get(spNumber).get("local_port")), callBackService);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
//
//
//        String account = this.sgipParamMap.get("SMS_SGIP_ACCOUNT");
//        String password = this.sgipParamMap.get("SMS_SGIP_PWD");
//        String port = this.sgipParamMap.get("SMS_SGIP_PORT");
    }

    public void setSgipParamMap(Map<String, String> params) {
        this.sgipParamMap = params;
    }

    public static Map<String, String> getSPInfoMap(String spNumber) {
        if (null == spInfoMaps) {
            return null;
        }
        return spInfoMaps.get(spNumber);
    }
}
