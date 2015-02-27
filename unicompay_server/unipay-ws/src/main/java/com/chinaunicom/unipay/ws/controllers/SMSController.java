package com.chinaunicom.unipay.ws.controllers;

/**
 * Created by jackj_000 on 2015/2/27 0027.
 */
public class SMSController {
    class SmsRequest extends SDKRequest{

    }
    public void pay(){

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
    }
    public void callback(){

    }
}
