package com.unicom.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.exception.ServiceException;
import com.unicom.sms.service.SMSSendService;
import com.unicom.sms.util.SMSContants;
import com.unicom.sms.util.SMSSendRequest;
import com.unicom.sms.util.SMSUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhaofrancis on 15/1/23.
 */
@Controller
@RequestMapping("/sms")
public class SMSController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSController.class);

    private static final String SEND_SMS_SUCCESS = "{\"code\":\"200\", \"message\":\"success\"}";
    @Autowired
    private SMSSendService smsSendService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    private String sendSMS(HttpServletRequest request, HttpServletResponse response) {
        try {
            SMSUtils.checkRequestHeader(request, "client_id", "client_secret");
            JSONObject jsonObject = this.checkRequestBody(request, "sp_number", "mobile_phone", "content");
            SMSSendRequest sendRequest = JSONObject.parseObject(jsonObject.toString(), SMSSendRequest.class);
            sendRequest.setClientId(request.getHeader("client_id"));
            sendRequest.setClientSecret(request.getHeader("client_secret"));

            smsSendService.sendSMS(sendRequest);
            return SMSController.SEND_SMS_SUCCESS;
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return SMSUtils.buildErrorResponse(e);
        } catch (Exception e) {
            return SMSUtils.buildErrorResponse();
        }
    }

    private JSONObject checkRequestBody(HttpServletRequest request, String... args) throws ServiceException {
        String requestBody = this.getRequestBody(request);
        if (StringUtils.isBlank(requestBody)) {
            throw new ServiceException(SMSContants.PARAM_ERROR_MESSAGE, SMSContants.PARAM_ERROR_CODE);
        }

        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        SMSUtils.checkRequestParams(jsonObject, args);
        return jsonObject;
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            byte[] byteArray = IOUtils.toByteArray(request.getInputStream());
            String charset = StringUtils.isNotBlank(request.getCharacterEncoding()) ? request.getCharacterEncoding() : "UTF-8";
            return new String(byteArray, charset);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
