package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.services.IBankService;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.chinaunicom.unipay.ws.utils.encrypt.AES;
import com.chinaunicom.unipay.ws.utils.encrypt.EncryUtil;
import com.chinaunicom.unipay.ws.utils.encrypt.RSA;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import static com.chinaunicom.unipay.ws.services.impl.YibaoService.PayAPI.*;

/**
 * User: Frank
 * Date: 2015/1/8
 * Time: 9:38
 */
@Service
public class YibaoService implements IBankService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");
    private final static String URLPREFIX = prop.get("payapi.urlprefix");
    private final static String URLSENDVALIDATECODE = URLPREFIX + SENDVALIDATECODE.getValue();
    private final static String URLUNBINDCARD = URLPREFIX + UNBINDCARD.getValue();
    private final static String URLQUERYORDER = URLPREFIX + QUERYORDER.getValue();
    private final static String URLBANKCARDCHECK = URLPREFIX + BANKCARDCHECK.getValue();
    private final static String URLBINDLIST = URLPREFIX + BINDLIST.getValue();
    private final static String URLCONFIRMPAY = URLPREFIX + CONFIRMPAY.getValue();

    private final static String yibaoPublicKey = prop.get("payapi.yibao_publickey");
    private final static String merchantPrivateKey = prop.get("payapi.merchant_privatekey");
    private final static String merchantaccount = prop.get("payapi.merchantaccount");

    @Resource
    private CloseableHttpClient c;

    @Override
    public PayResponse pay(Pay pay) throws Exception {
        return JSON.parseObject(request(pay.getCallurl(), pay, true), PayResponse.class);
    }

    @Override
    public VaCodeResponse sendVaCode(String orderid) throws Exception {

        if(StringUtils.isEmpty(orderid)){
            throw new IllegalArgumentException("缺少订单相关信息");
        }

        TreeMap<String,Object> map = new TreeMap<>();
        map.put("orderid",orderid);
        map.put("merchantaccount",merchantaccount);

        return JSON.parseObject(request(URLSENDVALIDATECODE, map, true), VaCodeResponse.class);
    }

    @Override
    public ConfirmResponse confirm(String orderid, String vaCode) throws Exception {

        if(StringUtils.isEmpty(orderid)){
            throw new IllegalArgumentException("缺少订单相关信息");
        }

        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("merchantaccount",merchantaccount);
        map.put("orderid", orderid);
        if (StringUtils.isNotEmpty(vaCode)) {
            map.put("validatecode", vaCode);
        }

        return JSON.parseObject(request(URLCONFIRMPAY, map, true), ConfirmResponse.class);
    }

    @Override
    public UnbindResponse unbindCard(String bindid, String identityid) throws Exception {

        if(StringUtils.isEmpty(bindid) || StringUtils.isEmpty(identityid)){
            throw new IllegalArgumentException("缺少绑卡相关信息");
        }

        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("merchantaccount",merchantaccount);
        map.put("bindid", bindid);
        map.put("identityid", identityid);
        map.put("identitytype", Pay.IDENTITY_TYPE_USERID);

        return JSON.parseObject(request(URLUNBINDCARD, map, true), UnbindResponse.class);
    }

    @Override
    public CardResponse cardinfo(String cardno) throws Exception {

        if(StringUtils.isEmpty(cardno)){
            throw new IllegalArgumentException("缺少银行卡号相关信息");
        }

        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("merchantaccount",merchantaccount);
        map.put("cardno", cardno);

        return JSON.parseObject(request(URLBANKCARDCHECK, map,true), CardResponse.class);
    }

    @Override
    public BindCardResponse bindcards(String identityid) throws Exception {

        if(StringUtils.isEmpty(identityid)){
            throw new IllegalArgumentException("缺少用户相关信息");
        }

        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("merchantaccount",merchantaccount);
        map.put("identityid", identityid);
        map.put("identitytype", Pay.IDENTITY_TYPE_USERID);

        return JSON.parseObject(request(URLBINDLIST, map, false), BindCardResponse.class);

    }

    @Override
    public CallbackResponse callback(String data, String key) throws Exception {

        boolean passSign = EncryUtil.checkDecryptAndSign(data, key, yibaoPublicKey, merchantPrivateKey);
        if (passSign) {
            data = AES.decryptFromBase64(data, RSA.decrypt(key, merchantPrivateKey));
            return JSON.parseObject(data, CallbackResponse.class);
        } else {
            throw new Exception("验签异常");
        }
    }

    private String request(String url, Object o,boolean post) throws Exception {
        TreeMap<String, Object> map = JSON.parseObject(JSON.toJSONString(o), TreeMap.class);
        return request(url, map, post);
    }

    private String request(String url, TreeMap map, boolean post) throws Exception {

        map.put("sign", EncryUtil.handleRSA(map, merchantPrivateKey));
        String tmp = JSON.toJSONString(map);

        String merchantAesKey = RandomUtil.getRandom(16);
        String data = AES.encryptToBase64(tmp, merchantAesKey);
        String encryptkey = RSA.encrypt(merchantAesKey, yibaoPublicKey);

        CloseableHttpResponse rsp = null;
        try {
            long time = System.currentTimeMillis();
            if (post) {
                HttpPost httpPost = new HttpPost(url);

                List<NameValuePair> nvp = new ArrayList<>();
                nvp.add(new BasicNameValuePair("merchantaccount", merchantaccount));
                nvp.add(new BasicNameValuePair("data", data));
                nvp.add(new BasicNameValuePair("encryptkey", encryptkey));
                httpPost.setEntity(new UrlEncodedFormEntity(nvp, "utf-8"));

                rsp = c.execute(httpPost);
            } else {
                StringBuilder sb = new StringBuilder(url).append("?")
                        .append(URLEncoder.encode("merchantaccount", "utf-8")).append("=").append(URLEncoder.encode(merchantaccount, "utf-8"))
                        .append("&").append(URLEncoder.encode("data", "utf-8")).append("=").append(URLEncoder.encode(data, "utf-8"))
                        .append("&").append(URLEncoder.encode("encryptkey", "utf-8")).append("=").append(URLEncoder.encode(encryptkey, "utf-8"));

                HttpGet httpGet = new HttpGet(sb.toString());
                rsp = c.execute(httpGet);
            }

            String result = IOUtils.toString(rsp.getEntity().getContent(), "utf-8");
            if (result.indexOf("error") < 0) {
                RespondJson rj = JSON.parseObject(result, RespondJson.class);
                String yb_encryptkey = rj.getEncryptkey();
                String yb_data = rj.getData();

                //进行数据验签
                boolean passSign = EncryUtil.checkDecryptAndSign(yb_data, yb_encryptkey, yibaoPublicKey, merchantPrivateKey);
                if (passSign) {
                    String yb_aeskey = RSA.decrypt(yb_encryptkey, merchantPrivateKey);
                    result = AES.decryptFromBase64(yb_data, yb_aeskey);
                } else {
                    throw new Exception("验签未通过");
                }
            }
            logger.debug("易宝请求完成|URL=" + url + "|用时=" + (System.currentTimeMillis() - time) + "ms|发送=" + tmp + "|获取=" + result);
            return result;
        } finally {
            if(rsp != null)
                rsp.close();
        }
    }

    private static class RespondJson {

        private String data;
        private String encryptkey;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getEncryptkey() {
            return encryptkey;
        }

        public void setEncryptkey(String encryptkey) {
            this.encryptkey = encryptkey;
        }

    }

    static enum PayAPI {

        /**
         * 手机wap网页收银台支付地址（通用），支持借记卡与信用卡(返回手机页面收银台方式)

         MOBILE_PAY("/mobile/pay/request"),


         * 手机wap网页收银台支付地址（借记卡）

         MOBILE_PAY_DEBIT("/mobile/pay/bankcard/debit/request"),


         * 手机wap网页收银台支付地址（信用卡）

         *MOBILE_PAY_CREDIT("/mobile/pay/bankcard/credit/request"),
         */

        /**
         * PC 网页收银台
         */
        PCWEB_PAY("/api/pay/request"),
        /**
         * 移动终端网页收银台
         */
        PAYWEB_PAY("/api/pay/request"),
        /**
         * 信用卡支付请求
         */
        CREDITCARDPAY("/api/bankcard/credit/pay/request"),
        /**
         * 储蓄卡支付请求
         */
        DEBITCARDPAY("/api/bankcard/debit/pay/request"),

        /**
         * 绑卡支付请求
         */
        BINDCARDPAY("/api/bankcard/bind/pay/request"),

        /**
         * 发送短信校验码
         */
        SENDVALIDATECODE("/api/validatecode/send"),

        /**
         * 校验短信验证码，确认支付
         */
        CONFIRMPAY("/api/async/bankcard/pay/confirm/validatecode"),

        /**
         * 解绑
         */
        UNBINDCARD("/api/bankcard/unbind"),
        /**
         * 查下支付结果
         */
        QUERYORDER("/api/query/order"),

        /**
         * 获取绑卡列表
         */
        BINDLIST("/api/bankcard/bind/list"),

        /**
         * 根据银行卡卡号检查银行卡是否可以使用一键支付
         */
        BANKCARDCHECK("/api/bankcard/check");

        private String value;

        private PayAPI(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class BindPay extends Pay {

        private final static String URLBINDCARDPAY = prop.get("payapi.urlprefix") + BINDCARDPAY.getValue();

        private String bindid;

        public BindPay(String bindid) {
            this.bindid = bindid;
        }

        @Override
        public String getCallurl() {
            return URLBINDCARDPAY;
        }

        public String getBindid() {
            return bindid;
        }

        public void setBindid(String bindid) {
            this.bindid = bindid;
        }
    }

    public static class CreditPay extends Pay {

        private final static String URLCREDITCARDPAY = prop.get("payapi.urlprefix") + CREDITCARDPAY.getValue();

        //信用卡有效期，格式：月月年年，例如：0715
        private String validthru;
        //信用卡背后的3位数字
        private String cvv2;
        //卡号
        private String cardno;
        //手机号
        private String phone;

        public CreditPay(String validthru, String cvv2, String cardno, String phone) {
            this.validthru = validthru;
            this.cvv2 = cvv2;
            this.cardno = cardno;
            this.phone = phone;
        }

        @Override
        public String getCallurl() {
            return URLCREDITCARDPAY;
        }

        public String getValidthru() {
            return validthru;
        }

        public void setValidthru(String validthru) {
            this.validthru = validthru;
        }

        public String getCvv2() {
            return cvv2;
        }

        public void setCvv2(String cvv2) {
            this.cvv2 = cvv2;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class DebitPay extends Pay {

        private final static String URLDEBITCARDPAY = prop.get("payapi.urlprefix") + DEBITCARDPAY.getValue();

        public final static String IDCARD_TYPE_IDENTITY = "01";
        //证件类型 01：身份证
        private String idcardtype = IDCARD_TYPE_IDENTITY;
        //证件号
        private String idcard;
        //证件人姓名
        private String owner;
        //卡号
        private String cardno;
        //手机号
        private String phone;

        public DebitPay(String idcard, String owner, String cardno, String phone) {
            this.idcard = idcard;
            this.owner = owner;
            this.cardno = cardno;
            this.phone = phone;
        }

        @Override
        public String getCallurl() {
            return URLDEBITCARDPAY;
        }

        public String getIdcardtype() {
            return idcardtype;
        }

        public void setIdcardtype(String idcardtype) {
            this.idcardtype = idcardtype;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
