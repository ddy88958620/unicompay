package com.unicom.order.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.common.bean.http.request.PageParam;
import com.unicom.order.bean.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sh-zhaogx3 on 2014/8/4.
 */
public class OrderResponse {

    @JSONField(name = "order_bizs")
    private List<OrderBiz> orderBizs;

    @JSONField(name = "paging_param")
    private PageParam pageParam;

    public OrderResponse() {
        super();
    }

    public OrderResponse(List<Order> orders, PageParam pageParam) {
        this();
        this.pageParam = pageParam;
        this.orderBizs = new ArrayList<OrderBiz>();
//        for (Order order : orders) {
//            OrderBiz orderBiz = new OrderBiz(order.getBizCd(), order.getEffectDate());
//
//            if (null != order.getCancelDate()) {
//                orderBiz.setCancelDate(order.getCancelDate());
//                orderBiz.setExpireDate(order.getExpireDate());
//            }
//            orderBizs.add(orderBiz);
//        }
    }

    public List<OrderBiz> getOrderBizs() {
        return orderBizs;
    }

    public void setOrderBizs(List<OrderBiz> orderBizs) {
        this.orderBizs = orderBizs;
    }

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    public class OrderBiz{

        @JSONField(name = "biz_cd")
        private String bizCd;

        //订购时间
        @JSONField(name = "effect_date", format = "yyyy-MM-dd HH:mm:ss")
        private Date effectDate;

        //退订时间
        @JSONField(name = "cancel_date", format = "yyyy-MM-dd HH:mm:ss")
        private Date cancelDate;

        //失效时间
        @JSONField(name = "expired_date", format = "yyyy-MM-dd HH:mm:ss")
        private Date expireDate;

        public OrderBiz() {}

        public OrderBiz(String bizCd, Date effectDate) {
            this.bizCd = bizCd;
            this.effectDate = effectDate;
        }

        public String getBizCd() {
            return bizCd;
        }

        public void setBizCd(String bizCd) {
            this.bizCd = bizCd;
        }

        public Date getEffectDate() {
            return effectDate;
        }

        public void setEffectDate(Date effectDate) {
            this.effectDate = effectDate;
        }

        public Date getCancelDate() {
            return cancelDate;
        }

        public void setCancelDate(Date cancelDate) {
            this.cancelDate = cancelDate;
        }

        public Date getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(Date expireDate) {
            this.expireDate = expireDate;
        }
    }

}
