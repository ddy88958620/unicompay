package com.unicom.vac.controller;

import com.alibaba.fastjson.JSONObject;
import com.unicom.vac.service.UnicomVACService;
import com.unicom.vac.util.VACConstants;
import com.unicom.vac.util.VACUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaofrancis on 15/2/11.
 */
@Controller
@RequestMapping(value = "/vac")
public class VACController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VACController.class);
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("request");

    @Autowired
    private UnicomVACService unicomVACService;

    private static final String SYSTEM_ERROR_RESPONSE = "{\"resultcode\":\"500\", resultmessage:\"system error\"}";

    @RequestMapping(value = "/consume", method = RequestMethod.POST)
    @ResponseBody
    public String consume(HttpServletRequest request, HttpServletResponse response) {
        Date requestDate = new Date();
        String requestBody = null;
        String responseBody = null;
        try {
            requestBody = IOUtils.toString(request.getInputStream());
            JSONObject bodyJson = JSONObject.parseObject(requestBody);
            this.checkParam(bodyJson, VACConstants.USER_ID, VACConstants.FLAG);

            Map<String, String> resultMap = unicomVACService.consume(bodyJson);
            responseBody = JSONObject.toJSONString(resultMap);
            return responseBody;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            responseBody = VACController.SYSTEM_ERROR_RESPONSE;
            return responseBody;
        } finally {
            Map<String, Object> requestRecordMap = new HashMap<String, Object>();
            requestRecordMap.put("request_body", requestBody);
            requestRecordMap.put("response_body", responseBody);
            requestRecordMap.put("request_date", requestDate);
            requestRecordMap.put("response_date", new Date());
            requestRecordMap.put("ip", VACUtil.getRequestIP(request));
            REQUEST_LOGGER.info(JSONObject.toJSONString(requestRecordMap));
        }
    }

    private void checkParam(JSONObject jsonObject, String ...args) {
        for (String param : args) {
            if (!jsonObject.containsKey(param)) {
                //TODO
            }
        }

        String mobilePhone = jsonObject.getString(VACConstants.USER_ID);
        if (mobilePhone.length() == 11) {
            jsonObject.replace(VACConstants.USER_ID, "86" + mobilePhone);
        }
    }
}
