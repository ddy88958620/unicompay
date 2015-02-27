package com.chinaunicom.unipay.ws.services;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2015/1/22 0022.
 */
public interface IPointService {

    //新积分支付接口
    public PointResponse consumePoint(String accessToken, String clientId, String clientSecret, String msgId, JSONObject body) throws Exception;


    static class PointResponse {

        public int consumeid;
        public String code;
        public String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getConsumeid() {
            return consumeid;
        }

        public void setConsumeid(int consumeid) {
            this.consumeid = consumeid;
        }

        public boolean isSucess(){return consumeid>0;}

    }
}
