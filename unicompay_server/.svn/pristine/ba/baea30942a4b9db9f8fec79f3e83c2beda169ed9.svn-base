package com.unicom.order.util;

import com.alibaba.fastjson.JSONObject;
import com.tydic.common.exception.ServiceException;
import com.tydic.common.interfaces.context.InterfaceManager;
import com.unicom.order.response.OrderResponse;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sh-zhaogx3 on 2014/8/15.
 */
public class ResponseUtil {
    private static final String VALIDATION_RESPONSE_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <paymessages> <checkOrderIdRsp>0</checkOrderIdRsp> <appname>沃游戏会员畅玩包</appname> <feename>沃游戏会员畅玩包（试用版）</feename> <payfee>6.00</payfee> <appdeveloper></appdeveloper> <gameaccount>{mobilePhone}</gameaccount> <macaddress></macaddress> <appid></appid> <ipaddress></ipaddress> <serviceid>140731047839</serviceid> <channelid>00012243</channelid> <cpid>86000136</cpid> <ordertime>{systemDate}</ordertime> <imei></imei> <appversion></appversion> </paymessages>";
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String buildGetOrderResponse(OrderResponse orderResponse) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OrderConstants.GET_USER_ORDER_BIZ, orderResponse);
        InterfaceManager.getInterfaceContext().setResBody(jsonObject.toJSONString());
        return ThreeDESCoder.encode(jsonObject.toJSONString());
    }


    public static String buildGetOrderErrorResponse(String errorCode, String errorMessage) throws Exception {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("err_cd", errorCode);
        resultMap.put("err_msg", errorMessage);
        jsonObject.put(OrderConstants.GET_USER_ORDER_BIZ, resultMap);
        InterfaceManager.getInterfaceContext().setResBody(jsonObject.toJSONString());
        return ThreeDESCoder.encode(jsonObject.toString());
    }

    public static String buildOrderMessage(String result) throws Exception {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("callbackRsp", result);
        jsonObject.put(OrderConstants.USER_ORDER_BIZ, resultMap);
        return jsonObject.toString();
    }

    public static String buildValidationResponse(String mobilePhone) {
        String systemDate = DATE_TIME_FORMAT.format(new Date());
        return VALIDATION_RESPONSE_STRING.replace("{mobilePhone}", mobilePhone).replace("{systemDate}", systemDate);
    }


    public static String buildXMLResponse(Map<String, String> dataMap, String rootName) {
        Document doc = DocumentHelper.createDocument();
        Element rootElement = new DefaultElement("");
        rootElement.setName(rootName);

        for (Map.Entry entry : dataMap.entrySet()) {
            Element element = new DefaultElement("");
            element.setName(String.valueOf(entry.getKey()));
            element.addText(String.valueOf(entry.getValue()));

            rootElement.add(element);
        }
        doc.setRootElement(rootElement);
        doc.setXMLEncoding("UTF-8");
        return doc.asXML();
    }

    public static String buildSuccessResponse(Map<String, Object> params) {
        return ResponseUtil.buildResponse("200", "success", params);
    }

    public static String buildErrorResponse(ServiceException e) {
        return ResponseUtil.buildResponse("400", e.getMessage(), null);
    }

    public static String buildErrorResponse() {
        return ResponseUtil.buildResponse("500", "error", null);
    }

    private static String buildResponse(String resultCode, String resultDesc, Map<String, Object> params) {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("resultcode", resultCode);
        results.put("resultdesc", resultDesc);

        if (null != params) {
            results.putAll(params);
        }

        String str = JSONObject.toJSONString(results);
        InterfaceManager.getInterfaceContext().setResBody(str);
        return str;
    }
}
