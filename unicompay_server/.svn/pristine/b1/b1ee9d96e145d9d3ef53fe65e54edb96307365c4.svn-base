package com.unicom.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tydic.common.constant.code.ErrorCodeConstants;
import com.tydic.common.exception.ServiceException;
import com.tydic.common.interfaces.context.InterfaceManager;
import com.unicom.order.bean.Order;
import com.unicom.order.request.OrderRequest;
import com.unicom.order.service.OrderService;
import com.unicom.order.util.OrderConstants;
import com.unicom.order.util.OrderUtil;
import com.unicom.order.util.RequestUtil;
import com.unicom.order.util.ResponseUtil;
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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by sh-zhaogx3 on 2014/7/31.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private static String ORDER_FAILED_RESPONSE;
    private static String ORDER_SUCCESS_RESPONSE;
    private static Properties properties = new Properties();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static JSONObject confJson = null;

    static {
        try {
            ORDER_FAILED_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <callbackRsp>0</callbackRsp>";
            ORDER_SUCCESS_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <callbackRsp>1</callbackRsp>";
            properties.load(OrderController.class.getClassLoader().getResourceAsStream("service.properties"));

            confJson = OrderUtil.parseJSONFile(OrderController.class.getClassLoader().getResourceAsStream("config.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private OrderService orderService;

    @RequestMapping(value="getOrderId", method = RequestMethod.POST)
    @ResponseBody
    public String getOrderId(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject requestJson = this.checkJsonRequestBody(request, "source", "serviceid", "channelid", "appversion", "userimsi", "macaddress", "imei");
            OrderRequest orderRequest = JSONObject.parseObject(requestJson.toJSONString(), OrderRequest.class);
            String orderId = orderService.generateOrderId(orderRequest, requestJson.toString());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderid", orderId);
            return ResponseUtil.buildSuccessResponse(params);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            String errorResponse = ResponseUtil.buildErrorResponse();
//            InterfaceManager.getInterfaceContext().setResBody(errorResponse);
            return errorResponse;
        }
    }

    @RequestMapping(value = "COCR", method = RequestMethod.POST)
    @ResponseBody
    public String checkOrder(HttpServletRequest request, HttpServletResponse response) {
        try {
            String serviceId = request.getParameter("serviceid");
            if (StringUtils.isNotBlank(serviceId) && serviceId.equals("validateorderid")) {
                LOGGER.info("validation");
                JSONObject requestJson = this.checkXMLRequestBody(request, OrderConstants.USER_ORDER_BIZ, "orderid", "userid");
                Order order = orderService.checkOrderId(requestJson.getString("orderid"), requestJson.getString("userid"));
                if (null != order) {
                    return this.buildOrderCheckResult(order, "0");
                } else {
                    return this.buildOrderCheckResult(null, "1");
                }
            } else {
                LOGGER.info("order or cancel");
                JSONObject requestJson = this.checkXMLRequestBody(request, OrderConstants.USER_ORDER_BIZ, "orderid", "payType", "hRet", "status");
                OrderRequest orderRequest = JSONObject.parseObject(requestJson.toJSONString(), OrderRequest.class);
                if (null != orderService.handleOrder(orderRequest)) {
                    return OrderController.ORDER_SUCCESS_RESPONSE;
                } else {
                    return OrderController.ORDER_FAILED_RESPONSE;
                }
            }

        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse();
        }
    }

    @RequestMapping(value = "/getOrderResult", method = RequestMethod.POST)
    @ResponseBody
    public String getOrderResult(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject requestJson = this.checkJsonRequestBody(request, "orderid");

            Order order = orderService.getOrderResult(requestJson.getString("orderid"));

            Map<String, Object> results = new HashMap<String, Object>();
            results.put("status", 1);

            if (null != order) {
                results.put("status", 0);
            }
            return ResponseUtil.buildSuccessResponse(results);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse();
        }
    }

    @RequestMapping(value = "/getOrderStatus", method = RequestMethod.POST)
    @ResponseBody
    public String getOrderStatus(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject requestJson = this.checkJsonRequestBody(request);
            OrderRequest orderRequest = JSONObject.parseObject(requestJson.toJSONString(), OrderRequest.class);

            Order order = orderService.getOrder(orderRequest);

            Map<String, Object> results = new HashMap<String, Object>();
            results.put("status", 1);

            if (null != order) {
                results.put("status", 0);
                results.put("orderid", order.getId());
            }
            return ResponseUtil.buildSuccessResponse(results);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse();
        }
    }

    @RequestMapping(value = "/getOff", method = RequestMethod.POST)
    @ResponseBody
    public String getOff(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject requestJson = this.checkJsonRequestBody(request, "channelid", "userimsi");
            OrderRequest orderRequest = JSONObject.parseObject(requestJson.toJSONString(), OrderRequest.class);

            String channelId = orderRequest.getChannelid();
            String offChannels = OrderController.confJson.getString("discount_channel");
            if (!channelId.contains(",") && !offChannels.contains(channelId)) {
                return this.buildOffResult(null, false);
            }

            Order order = orderService.getOrder(orderRequest);
            return this.buildOffResult(order, true);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse();
        }
    }

    private JSONObject checkJsonRequestBody(HttpServletRequest request, String... params) throws Exception {
        String requestBody = InterfaceManager.getInterfaceContext().getReqBody();
//        String requestBody = this.getRequestBody(request);
        JSONObject jsonObject = JSON.parseObject(requestBody);
        if (null == jsonObject) {
            throw new ServiceException(OrderConstants.ERROR_MESSAGE_PARAM, ErrorCodeConstants.PAR_NOT_COMPLETE_ERROR_CODE);
        }
        RequestUtil.checkRequestParams(jsonObject, params);
        return jsonObject;
    }

    private JSONObject checkXMLRequestBody(HttpServletRequest request, String rootName, String... params) throws Exception {
        String requestBody = InterfaceManager.getInterfaceContext().getReqBody();
//        String requestBody = this.getRequestBody(request);
        Map<String, String> requestBodyMap = RequestUtil.parseXMLRequest(requestBody);
        if (null == requestBodyMap || requestBodyMap.isEmpty()) {
            throw new ServiceException(OrderConstants.ERROR_MESSAGE_PARAM, ErrorCodeConstants.PAR_NOT_COMPLETE_ERROR_CODE);
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(requestBodyMap));
        RequestUtil.checkRequestParams(jsonObject, params);
        return jsonObject;
    }

    private String buildOrderCheckResult(Order order, String result) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("checkOrderIdRsp", result);
        if (null != order) {
            JSONObject jsonObject = JSON.parseObject(order.getContext());
            dataMap.put("appname", OrderController.confJson.getString("app_name"));
            dataMap.put("feename", OrderController.confJson.getJSONObject(order.getServiceId()).getString("fee_name"));
            dataMap.put("macaddress", jsonObject.getString("macaddress"));
            dataMap.put("appid", OrderController.confJson.getString("app_id"));
            dataMap.put("serviceid", jsonObject.getString("serviceid"));
            dataMap.put("channelid", jsonObject.getString("channelid"));
            dataMap.put("cpid", OrderController.confJson.getString("cp_id"));
            dataMap.put("ordertime", OrderController.simpleDateFormat.format(order.getCreateTime()));
            dataMap.put("imei", jsonObject.getString("imei"));
            dataMap.put("appversion", jsonObject.getString("appversion"));
        }

        return ResponseUtil.buildXMLResponse(dataMap, "paymessages");
    }

    /**
     *
     * @param order
     * @param isSupport whether supprot discount
     * @return
     */
    private String buildOffResult(Order order, boolean isSupport) {
        int off = 100;
        if (null != order) {
            off = OrderController.confJson.getJSONObject(order.getServiceId()).getInteger("off");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("off", off);
        params.put("is_support", isSupport ? "1" : "0");
        return ResponseUtil.buildSuccessResponse(params);
    }
}
