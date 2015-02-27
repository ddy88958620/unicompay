package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.ISMSService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.TreeMap;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.MINUTE;

/**
 * Created by jackj_000 on 2015/2/27 0027.
 */
public class SMSController extends WSController{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource RedisUtil ru;
    @Resource
    ISMSService iss;
    @Resource ICPService cps;
    @Resource IMessageService ms;
    class SmsRequest extends SDKRequest{

    }
    public void pay() throws Exception {
        SmsRequest sr  = getJSONObject(SmsRequest.class);

    }
    class CallbackResponse{
        private String payid;
        private String linkid;
        private int status;
        private String chargemsg;
        private String price;
        private String md5;

        public String getPayid() {
            return payid;
        }

        public void setPayid(String payid) {
            this.payid = payid;
        }

        public String getLinkid() {
            return linkid;
        }

        public void setLinkid(String linkid) {
            this.linkid = linkid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getChargemsg() {
            return chargemsg;
        }

        public void setChargemsg(String chargemsg) {
            this.chargemsg = chargemsg;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public boolean isSuccess(){return status == 0;}
    }
    class SmsResponse{
        private String status;
        private String errormsg;
        public SmsResponse(){}
        public SmsResponse(String status,String errormsg){
            this.status = status;
            this.errormsg = errormsg;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrormsg() {
            return errormsg;
        }

        public void setErrormsg(String errormsg) {
            this.errormsg = errormsg;
        }
    }

    public void callback() throws Exception {
        CallbackResponse cr = getJSONObject(CallbackResponse.class);
        Map<String,String> map = JSON.parseObject(JSON.toJSONString(cr),new TypeReference<TreeMap>(){});
        String verify = VerifyUtil.getVerify(map);
        if(!verify.equals(cr.getMd5()))
            return;
        String orderid = cr.getLinkid();
        if(ru.getSet(RedisUtil.Table.CALLBACK.getKey(orderid), "y", 30 * MINUTE) != null) {
            logger.debug("订单[" + orderid + "]重复发送支付结果通知");
            return;
        }

        Order order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(cr.getPayid())
                    .setStatus(cr.isSuccess() ? 0 : 1)
                    .setPayresult(cr.isSuccess() ? "00000" : "1")
                    .setErrorcode(cr.isSuccess() ? "00000" : "1")
                    .update();
        } else {
            logger.error("[" + orderid + "]无法在订单库中找到");
        }

        if(cr.getStatus() != 0) {
            renderJson(new SmsResponse("ERROR", "失败"));
            return;
        }
         renderJson(new SmsResponse("OK","成功"));

        //通知CP
        ICPService.Notification n = new ICPService.Notification();
        n.setAppid(order.getProductid());
        n.setConsumecode(order.getPointid());
        n.setCpid(order.getCpid());
        n.setCporderid(order.getOrderid_3rd());
        n.setOrderid(order.getOrderid());
        n.setFid(order.getChannelid());
        n.setOrdertime(order.getPaytime());
        n.setPayfee(String.valueOf(order.getPayfee()));
        n.setStatus(order.getStatus());
        n.setPaytype(7);
        try {
            cps.sendNotification(n);
        } catch (Exception e) {
            logger.error("CP通知失败", e);
        }

        //通知消息系统
        IMessageService.Message msg = new IMessageService.Message();
        msg.setCpid(order.getCpid());
        msg.setPayresult(order.getPayresult());
        msg.setOrderid(order.getOrderid());
        msg.setCporderid(order.getOrderid_3rd());
        msg.setPayfee(order.getPayfee());
        msg.setPaytime(order.getPaytime());
        msg.setPaytype(IMessageService.Message.PayType.SMS.getValue());
        msg.setServiceid(order.getServicekey());
        msg.setStatus(order.getStatus());
        try {
            ms.notify(msg);
        } catch (Exception e) {
            logger.error("消息通知失败", e);
        }

    }
}
