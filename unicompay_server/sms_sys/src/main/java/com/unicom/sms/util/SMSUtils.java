package com.unicom.sms.util;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.bean.SMSBean;
import com.unicom.sms.bean.SgipContext;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.queue.task.SendMessageTask;
import com.unicom.sms.queue.task.UpnoticeMessageTask;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public class SMSUtils {
    public static String formatErrorSMS(SMSBean smsBean, Exception e) {
        StringBuffer stringBuffer = new StringBuffer(SMSUtils.formatSMS(smsBean));
        stringBuffer.append("|");
        stringBuffer.append(e.getMessage());
        return stringBuffer.toString();
    }

    public static String formatSMS(SMSBean smsBean) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(smsBean.getMobilePhone());
        stringBuffer.append("|");
        stringBuffer.append(smsBean.getSpNumber());
        stringBuffer.append("|");
        stringBuffer.append(smsBean.getContent());
        stringBuffer.append("|");
        stringBuffer.append(smsBean.getReceiveTime());
        return stringBuffer.toString();
    }

    public static String formatSMS(UpnoticeMessageTask upnoticeMessageTask) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(upnoticeMessageTask.getMobilePhone());
        stringBuffer.append("|");
        stringBuffer.append(upnoticeMessageTask.getSpNumber());
        stringBuffer.append("|");
        stringBuffer.append(upnoticeMessageTask.getContent());
        stringBuffer.append("|");
        stringBuffer.append(upnoticeMessageTask.getReceiveTime());
        return stringBuffer.toString();
    }

    public static String formatErrorSMS(UpnoticeMessageTask upnoticeMessageTask, Exception e) {
        StringBuffer stringBuffer = new StringBuffer(SMSUtils.formatSMS(upnoticeMessageTask));
        stringBuffer.append("|");
        stringBuffer.append(e.getMessage());
        return stringBuffer.toString();
    }
    public static String formatSMS(SendMessageTask upnoticeMessageTask) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(upnoticeMessageTask.getMobilePhone());
        stringBuffer.append("|");
        stringBuffer.append(upnoticeMessageTask.getSpNumber());
        stringBuffer.append("|");
        stringBuffer.append(upnoticeMessageTask.getContent());
        stringBuffer.append("|");
        stringBuffer.append(System.currentTimeMillis());
        return stringBuffer.toString();
    }

    public static String formatErrorSMS(SendMessageTask upnoticeMessageTask, Exception e) {
        StringBuffer stringBuffer = new StringBuffer(SMSUtils.formatSMS(upnoticeMessageTask));
        stringBuffer.append("|");
        stringBuffer.append(e.getMessage());
        return stringBuffer.toString();
    }

    public static void checkRequestParams(JSONObject jsonObject, String... params) throws ServiceException {
        for (String param : params) {
            if (!jsonObject.containsKey(param)) {
                throw new ServiceException(SMSContants.PARAM_ERROR_MESSAGE, SMSContants.PARAM_ERROR_CODE);
            }
        }
    }

    public static void checkRequestHeader(HttpServletRequest request, String... params) throws ServiceException {
        for (String param : params) {
            if (StringUtils.isBlank(request.getHeader(param))) {
                throw new ServiceException(SMSContants.PARAM_ERROR_MESSAGE, SMSContants.PARAM_ERROR_CODE);
            }
        }
    }

    public static String buildErrorResponse(ServiceException e) {
        return SMSUtils.buildResponse(e.getErrCode(), e.getMessage());
    }

    public static String buildErrorResponse() {
        return SMSUtils.buildResponse("500", "system error");
    }

    private static String buildResponse(String resultCode, String resultDesc) {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("code", resultCode);
        results.put("message", resultDesc);

//        if (null != params) {
//            results.putAll(params);
//        }

        return JSONObject.toJSONString(results);
    }
}
