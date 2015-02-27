package com.chinaunicom.unipay.ws.services;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by jackj_000 on 2015/2/10 0010.
 */
public interface IWeiXinService {

    static class IWeiXin{

        //微信分配的公众账号ID
        private String appid;
        //商品或支付单简要描述
        private String body;
        //微信支付分配的商户号
        private String mch_id;
        //随机字符串
        private String nonce_str;
        //接收微信支付异步通知回调地址
        private String notify_url;
        //二维码中包含的商品ID，商户自行定义
        private String product_id;
        //商户订单号
        private String out_trade_no;
        //APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
        private String spbill_create_ip;
        //订单总金额
        private int total_fee;
        //交易类型
        private String trade_type;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getSpbill_create_ip() {
            return spbill_create_ip;
        }

        public void setSpbill_create_ip(String spbill_create_ip) {
            this.spbill_create_ip = spbill_create_ip;
        }

        public int getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(int total_fee) {
            this.total_fee = total_fee;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(String trade_type) {
            this.trade_type = trade_type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
    static class IWeiXinResponse{

        private String return_code;
        private String return_msg;

        private String err_code;
        private String err_code_des;
        //公众账号ID
        private String appid;
        //商户号
        private String mch_id;
        //随机字符串
        private String nonce_str;
        private String sign;
        //业务结果
        private String result_code;
        //交易类型
        private String trade_type;
        //预支付交易会话标识
        private String prepay_id;
        //二维码链接
        private String code_url;

        public String getReturn_code() {
            return return_code;
        }

        public void setReturn_code(String return_code) {
            this.return_code = return_code;
        }

        public String getReturn_msg() {
            return return_msg;
        }

        public void setReturn_msg(String return_msg) {
            this.return_msg = return_msg;
        }

        public String getErr_code() {
            return err_code;
        }

        public void setErr_code(String err_code) {
            this.err_code = err_code;
        }

        public String getErr_code_des() {
            return err_code_des;
        }

        public void setErr_code_des(String err_code_des) {
            this.err_code_des = err_code_des;
        }

        public String getCode_url() {
            return code_url;
        }

        public void setCode_url(String code_url) {
            this.code_url = code_url;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(String trade_type) {
            this.trade_type = trade_type;
        }

        public String getPrepay_id() {
            return prepay_id;
        }

        public void setPrepay_id(String prepay_id) {
            this.prepay_id = prepay_id;
        }

        public boolean isSuccess(){
            return "SUCCESS".equals(return_code) ? "SUCCESS".equals(result_code) :false;
        }
    }

    public IWeiXinResponse getQrcode(IWeiXin wx) throws Exception;

    static class IWeiXinCallBackRequest extends IWeiXinResponse{

        //用户标识
        private String openid;
        //is_subscribe
        private String is_subscribe;
        //付款银行
        private String bank_type;
        //总金额
        private int total_fee;
        //现金支付金额
        private int cash_fee;
        //微信支付订单号
        private String transaction_id;
        //商户订单号
        private String out_trade_no;
        //支付完成时间
        private String time_end;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getIs_subscribe() {
            return is_subscribe;
        }

        public void setIs_subscribe(String is_subscribe) {
            this.is_subscribe = is_subscribe;
        }

        public String getBank_type() {
            return bank_type;
        }

        public void setBank_type(String bank_type) {
            this.bank_type = bank_type;
        }

        public int getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(int total_fee) {
            this.total_fee = total_fee;
        }

        public int getCash_fee() {
            return cash_fee;
        }

        public void setCash_fee(int cash_fee) {
            this.cash_fee = cash_fee;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getTime_end() {
            return time_end;
        }

        public void setTime_end(String time_end) {
            this.time_end = time_end;
        }

        public boolean isSuccess(){
            return "SUCCESS".equals(getReturn_code()) ? "SUCCESS".equals(getResult_code()) :false;
        }

    }
    static class IWeiXinCallBackResponse{

        private String return_code;
        private String return_msg;

        public IWeiXinCallBackResponse(String return_code, String return_msg) {
            this.return_code = return_code;
            this.return_msg = return_msg;
        }

        public String getReturn_code() {
            return return_code;
        }

        public void setReturn_code(String return_code) {
            this.return_code = return_code;
        }

        public String getReturn_msg() {
            return return_msg;
        }

        public void setReturn_msg(String return_msg) {
            this.return_msg = return_msg;
        }
    }
}
