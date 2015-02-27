package com.chinaunicom.unipay.ws.services;

/**
 * User: Frank
 * Date: 2015/1/20
 * Time: 11:14
 */
public interface IMessageService {

    public boolean notify(Message msg) throws Exception;

    public static class Message {
        private String paytime;
        private String orderid;
        private String cporderid;
        private int paytype;
        private String cpid;
        private String serviceid;
        private int payfee;
        private String payresult;
        private int status;


        /*
            0-短代，1-在线VAC，2-老版在线VAC，3-渠道支付，4-代支付， 5-短短代，6-按次代缴，7-真包月，8-代支付按次代缴，9-代支付真包月，10-cpApi支付，11-tv支付，12-易宝支付, 13-19pay, 14-沃币充值, 101-支付宝， 102-神州付，103-爱游戏，104-和游戏, 105-和游戏短代，106-和游戏在线，107-天翼支付，108-微软支付等, 109-支付宝扫码
        */
        public static enum PayType {

            YIBAO(12),//易宝支付
            PAY19(13),//19pay
            WOBI(14),//沃币充值
            ALIPAY(101),//支付宝
            ALIPAY_QCODE(109),//支付宝扫码
            TELECOM(111),//电信验证码
            MOBILE(112),//移动验证码
            WEIXIN_QCODE(113),//微信宝扫码
            SMS(114);//微信宝扫码

            private final int value;

            PayType(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }
        }

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public String getPaytime() {
            return paytime;
        }

        public void setPaytime(String paytime) {
            this.paytime = paytime;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public int getPaytype() {
            return paytype;
        }

        public void setPaytype(int paytype) {
            this.paytype = paytype;
        }

        public String getCpid() {
            return cpid;
        }

        public void setCpid(String cpid) {
            this.cpid = cpid;
        }

        public String getServiceid() {
            return serviceid;
        }

        public void setServiceid(String serviceid) {
            this.serviceid = serviceid;
        }

        public int getPayfee() {
            return payfee;
        }

        public void setPayfee(int payfee) {
            this.payfee = payfee;
        }

        public String getPayresult() {
            return payresult;
        }

        public void setPayresult(String payresult) {
            this.payresult = payresult;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


}
