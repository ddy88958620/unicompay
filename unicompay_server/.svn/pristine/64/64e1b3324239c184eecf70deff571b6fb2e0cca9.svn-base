package com.unicom.order.service.impl;

import com.tydic.common.constant.code.ErrorCodeConstants;
import com.tydic.common.exception.ServiceException;
import com.unicom.order.bean.Order;
import com.unicom.order.controller.OrderController;
import com.unicom.order.dao.OrderDAO;
import com.unicom.order.request.OrderRequest;
import com.unicom.order.service.OrderService;
import com.unicom.order.util.OrderConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by sh-zhaogx3 on 2014/7/31.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final String PAY_SUCCESS_STATUS = "00000";
    private static final String PAY_SUCCESS_RESULTS = "0";
    private static final String NEW_CODE = "2";
    private static final String CANCEL_CODE = "4";
//    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final String SOURCE = "0123456789";
    private static final int RANDOM_ORDER_ID_LENGTH = 9;
    private static final int RANDOM_ORDER_ID_FULL_LENGTH = 24;

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public String generateOrderId(OrderRequest orderRequest, String context) throws ServiceException {
        if (StringUtils.isBlank(orderRequest.getImsi())) {
            LOGGER.error("imsi is null");
            throw new ServiceException(OrderConstants.PARAM_IS_NULL);
        }

        Order tempOrder = orderDAO.getOrderByKey(orderRequest.getImsi());
        if (null != tempOrder && (OrderConstants.ORDER_STATUS_CHECKED.equals(tempOrder.getStatus()) || OrderConstants.ORDER_STATUS_INIT.equals(tempOrder.getStatus()))) {
            LOGGER.error("order status: " + tempOrder.getStatus() + " orderid: " + tempOrder.getId());
            throw new ServiceException(OrderConstants.ERROR_STATUS);
        }

        String orderId = orderRequest.getImsi() + this.randomOrderId(OrderServiceImpl.RANDOM_ORDER_ID_LENGTH);
        orderRequest.setOrderId(orderId);
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderConstants.ORDER_STATUS_INIT);
        order.setImsi(orderRequest.getImsi());
        order.setServiceId(orderRequest.getServiceId());
        order.setContext(context);
        order.setCreateTime(new Date());

        orderDAO.saveOrder(order);
        return orderId;
    }

    @Override
    public Order checkOrderId(String orderId, String mobilePhone) throws ServiceException {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(mobilePhone)) {
            LOGGER.error("orderid or mobile phone is null");
            throw new ServiceException(OrderConstants.PARAM_IS_NULL);
        }
        Order order = orderDAO.getOrderById(orderId);
        if (null == order || StringUtils.isBlank(order.getStatus()) || !OrderConstants.ORDER_STATUS_INIT.equals(order.getStatus())) {
            LOGGER.error("order is null or status is wrong");
            return null;
        }

        order.setPhoneNumber(mobilePhone);
        order.setStatus(OrderConstants.ORDER_STATUS_CHECKED);
        orderDAO.saveOrder(order);
        orderDAO.saveKeyOrder(mobilePhone, order.getId());

        return order;
    }

    @Override
    public Order handleOrder(OrderRequest orderRequest) {
        if (null == orderRequest) {
            throw new ServiceException(OrderConstants.ERROR_MESSAGE_PARAM, ErrorCodeConstants.PAR_NOT_COMPLETE_ERROR_CODE);
        }

        if (StringUtils.isBlank(orderRequest.getStatus()) || !OrderServiceImpl.PAY_SUCCESS_STATUS.equals(orderRequest.getStatus()) || !OrderServiceImpl.PAY_SUCCESS_RESULTS.equals(orderRequest.getPayResult())) {
            LOGGER.error("pay status: " + orderRequest.getStatus() + ", pay result: " + orderRequest.getPayResult());
            throw new ServiceException(OrderConstants.PAY_FAILED_MESSAGE);
        }

        Order order = orderDAO.getOrderById(orderRequest.getOrderId());

        if (null == order) {
            LOGGER.error("order is null");
            throw new ServiceException(OrderConstants.ORDER_IS_NULL);
        }

        //new
        if (OrderServiceImpl.NEW_CODE.equals(orderRequest.getPayType())) {
            //new order
            if (OrderConstants.ORDER_STATUS_CHECKED.equals(order.getStatus())) {
                order.setStatus(OrderConstants.ORDER_STATUS_FINISH);
                order.setEffectDate(new Date());

                orderDAO.saveOrder(order);
                orderDAO.saveKeyOrder(order.getPhoneNumber(), order.getId());
                orderDAO.saveKeyOrder(order.getImsi(), order.getId());
            } else if (OrderConstants.ORDER_STATUS_FINISH.equals(order.getStatus())) {
                //again
                String newOrderId = this.randomOrderId(OrderServiceImpl.RANDOM_ORDER_ID_FULL_LENGTH);
                String oldId = order.getId();

                order.setId(newOrderId);
                orderDAO.saveOrder(order);

                order = new Order();
                order.setId(oldId);
                order.setEffectDate(new Date());
                order.setStatus(OrderConstants.ORDER_STATUS_FINISH);
                order.setPrevOrderId(newOrderId);

                orderDAO.saveOrder(order);
            }
        } else if (OrderServiceImpl.CANCEL_CODE.equals(orderRequest.getPayType())) {
            if (!OrderConstants.ORDER_STATUS_FINISH.equals(order.getStatus())) {
                LOGGER.error("error status: " + order.getStatus());
                throw new ServiceException(OrderConstants.ERROR_STATUS);
            }

            order.setStatus(OrderConstants.ORDER_STATUS_CANCEL);
            order.setCancelDate(new Date());
            orderDAO.saveOrder(order);
        }
        return order;

    }

    @Override
    public Order getOrder(OrderRequest orderRequest) throws ServiceException {
        if (null == orderRequest || (StringUtils.isBlank(orderRequest.getImsi()) && StringUtils.isBlank(orderRequest.getMobilePhone()))) {
            LOGGER.error("imsi or mobile phone is null");
            throw new ServiceException(OrderConstants.PARAM_IS_NULL);
        }

        String key = orderRequest.getMobilePhone();
        if (StringUtils.isNotBlank(orderRequest.getImsi())) {
            key = orderRequest.getImsi();
        }

        Order order = orderDAO.getOrderByKey(key);
        if (order != null && OrderConstants.ORDER_STATUS_FINISH.equals(order.getStatus())) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(order.getEffectDate());
            int oldMonth = calendar.get(Calendar.MONTH);

            if ((month - oldMonth < 1) || (month - oldMonth == 1 && day <= 10)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public Order getOrderResult(String orderId) throws ServiceException {
        if (StringUtils.isBlank(orderId)) {
            LOGGER.error("orderid is null");
            throw new ServiceException(OrderConstants.PARAM_IS_NULL);
        }

        Order order = orderDAO.getOrderById(orderId);
        if (null == order) {
            throw new ServiceException(OrderConstants.ORDER_IS_NULL);
        }

        if (!OrderConstants.ORDER_STATUS_FINISH.equals(order.getStatus()) && !OrderConstants.ORDER_STATUS_CANCEL.equals(order.getStatus())) {
            return null;
        }
        return order;
    }

    private String randomOrderId(int length) {
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(10);
            buf.append(OrderServiceImpl.SOURCE.charAt(num));
        }
        return buf.toString();
    }
}
