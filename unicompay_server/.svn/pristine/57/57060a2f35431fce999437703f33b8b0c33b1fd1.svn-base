package com.unicom.order.dao.impl;

import com.unicom.order.bean.Order;
import com.unicom.order.cache.RedisManager;
import com.unicom.order.dao.OrderDAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by sh-zhaogx3 on 2014/7/31.
 */
@Repository
public class OrderDAOImpl implements OrderDAO {
    @Autowired
    private RedisManager redisManager;

    @Override
    public void saveOrder(Order order) {
        if (null == order) {
            return;
        }
        redisManager.put(order.getId(), order);
    }

//    @Override
//    public void updateOrder(Order order) {
//        if (null == order) {
//            return;
//        }
//        redisManager.put(order.getId(), order);
//    }

    @Override
    public void saveKeyOrder(String key, String orderId) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(orderId)) {
            return;
        }
        redisManager.put(key, orderId);
    }

    @Override
    public Order getOrderById(String orderId) {
        return (Order)redisManager.get(orderId);
    }

    @Override
    public Order getOrderByKey(String key) {
        return this.getOrderById(String.valueOf(redisManager.get(key)));
    }
}
