package com.unicom.order.service;

import com.tydic.common.exception.ServiceException;
import com.unicom.order.bean.Order;
import com.unicom.order.request.OrderRequest;

/**
 * Created by sh-zhaogx3 on 2014/7/31.
 */
public interface OrderService {
    public Order handleOrder(OrderRequest orderRequest) throws ServiceException;

    public Order getOrder(OrderRequest orderRequest) throws ServiceException;

    public String generateOrderId(OrderRequest orderRequest, String context) throws ServiceException;

    public Order checkOrderId(String orderId, String mobilePhone) throws ServiceException;

    public Order getOrderResult(String orderId) throws ServiceException;
}
