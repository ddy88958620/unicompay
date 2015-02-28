package com.chinaunicom.unipay.ws.services;

/**
 * Created by jackj_000 on 2015/2/27 0027.
 */
public interface ISMSService {
    class SmsRequest{
        private String merchantid;
        private String linkid;
        private String appid;
        private String paypoint;
        private String price;
        private String ip;
        private String imsi;
        private String mac;
        private String notifyurl;
        private String ordertime;
        private String md5;

        public String getMerchantid() {
            return merchantid;
        }

        public void setMerchantid(String merchantid) {
            this.merchantid = merchantid;
        }

        public String getLinkid() {
            return linkid;
        }

        public void setLinkid(String linkid) {
            this.linkid = linkid;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPaypoint() {
            return paypoint;
        }

        public void setPaypoint(String paypoint) {
            this.paypoint = paypoint;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getNotifyurl() {
            return notifyurl;
        }

        public void setNotifyurl(String notifyurl) {
            this.notifyurl = notifyurl;
        }

        public String getOrdertime() {
            return ordertime;
        }

        public void setOrdertime(String ordertime) {
            this.ordertime = ordertime;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
    class SmsResponse{
        private String status;
        private String errormsg;
        private String payid;
        private String sendnum;
        private String smscontent;
        private String smsaddress;

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

        public String getPayid() {
            return payid;
        }

        public void setPayid(String payid) {
            this.payid = payid;
        }

        public String getSendnum() {
            return sendnum;
        }

        public void setSendnum(String sendnum) {
            this.sendnum = sendnum;
        }

        public String getSmscontent() {
            return smscontent;
        }

        public void setSmscontent(String smscontent) {
            this.smscontent = smscontent;
        }

        public String getSmsaddress() {
            return smsaddress;
        }

        public void setSmsaddress(String smsaddress) {
            this.smsaddress = smsaddress;
        }

        public boolean isSuccess(){return "OK".equals(status);}
    }
    public SmsResponse charge(SmsRequest sr) throws Exception;
}
