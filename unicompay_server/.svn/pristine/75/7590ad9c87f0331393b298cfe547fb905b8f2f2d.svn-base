package com.chinaunicom.unipay.ws.services;

/**
 * User: Frank
 * Date: 2015/1/9
 * Time: 13:49
 */
public interface ICPService {

    public void sendNotification(Notification n) throws Exception;
    public boolean checkOrder(String consumecode, String cpid, String cporderid) throws Exception;
    public boolean checkAuth(String consumecode, String cpid, String channelid) throws Exception;

    public static class Notification {

        private String orderid;
        private String cporderid;
        private String ordertime;
        private String cpid;
        private String appid;
        private String fid;
        private String consumecode;
        private String payfee;
        private int paytype;
        private int status;
        private int sendtype;

        public int getPaytype() {
            return paytype;
        }

        public void setPaytype(int paytype) {
            this.paytype = paytype;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public int getSendtype() {
            return sendtype;
        }

        public void setSendtype(int sendtype) {
            this.sendtype = sendtype;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getOrdertime() {
            return ordertime;
        }

        public void setOrdertime(String ordertime) {
            this.ordertime = ordertime;
        }

        public String getCpid() {
            return cpid;
        }

        public void setCpid(String cpid) {
            this.cpid = cpid;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public String getConsumecode() {
            return consumecode;
        }

        public void setConsumecode(String consumecode) {
            this.consumecode = consumecode;
        }

        public String getPayfee() {
            return payfee;
        }

        public void setPayfee(String payfee) {
            this.payfee = payfee;
        }
    }
}
