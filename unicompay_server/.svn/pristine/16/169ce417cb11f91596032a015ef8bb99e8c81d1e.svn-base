package com.unicom.order.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tydic.common.bean.http.request.HttpRequest;
import com.tydic.common.bean.http.request.PageParam;
import com.tydic.common.constant.code.CodeTypeConstant;
import com.tydic.common.constant.code.ErrorCodeConstants;
import com.tydic.common.exception.ServiceException;
import com.tydic.utils.ObjectIsNull;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sh-zhaogx3 on 2014/8/15.
 */
public class RequestUtil {

    public static void checkRequestParams(JSONObject jsonObject, String... params) throws ServiceException {
        for (String param : params) {
            if (ObjectIsNull.check(jsonObject.getString(param))) {
                throw new ServiceException(OrderConstants.ERROR_MESSAGE_PARAM, ErrorCodeConstants.PAR_NOT_COMPLETE_ERROR_CODE);
            }
        }
    }


    public static Map<String, String> parseXMLRequest(String requestBody) {
        Map<String, String> requestBodyMap = new HashMap<String , String>();
        if (StringUtils.isBlank(requestBody)) {
            return requestBodyMap;
        }
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(requestBody);
            Element rootElement = doc.getRootElement();
            Iterator iterator = rootElement.elementIterator();
            while(iterator.hasNext()) {
                Element element = (Element)iterator.next();
                requestBodyMap.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return requestBodyMap;
    }


    public static HttpRequest parseRequest(JSONObject requestJson, Class<? extends HttpRequest> requestClass) {
        HttpRequest httpRequest = JSON.parseObject(requestJson.toJSONString(), requestClass);
        JSONObject pageJson = requestJson.getJSONObject(CodeTypeConstant.JSON_PAGE_PARAM_KEY);
        if (null != pageJson) {
            httpRequest.setPageParam(JSON.parseObject(pageJson.toJSONString(), PageParam.class));
        }
        return httpRequest;
    }
}
