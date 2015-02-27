package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.encrypt.RSA;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.HOUR;

/**
 * Created by lynne on 2015/1/21.
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class AliPayController extends WSController {

    private static final Logger logger = LoggerFactory.getLogger(AliPayController.class);
    private static final Prop prop = PropKit.use("payapi.properties", "utf-8");
    private static final String NOTIFY_URL= prop.get("alipay.notifyurl");
    private static final String ALI_PUBLICKEY = prop.get("alipay.ali_publickey");
    private static final String MERCHENT_PRIVATEKEY = prop.get("alipay.merchant_privatekey");
    private final static String PARTNER = prop.get("alipay.partner");
    private final static String SELLER_ID = prop.get("alipay.seller_id");
    private static final String HTTPS_VERIFY_URL = prop.get("alipay.verify_url");

    @Resource ICPService cps;
    @Resource IMessageService ms;
    @Resource RedisUtil ru;

    public void pay() throws Exception {

        final AlipayRequest pay = getJSONObject(AlipayRequest.class);

        //临时订单入库
        final Order order = createOrder(pay);

        //生成满足支付宝格式的参数，并且签名
        final String orderinfo = new StringBuilder()
                .append("partner=\"").append(PARTNER).append("\"")
                .append("&seller_id=\"").append(SELLER_ID).append("\"")
                .append("&out_trade_no=\"").append(order.getOrderid()).append("\"")
                .append("&subject=\"").append(order.getProductname()).append("\"")
                .append("&body=\"").append(order.getProductname()).append("\"")
                .append("&total_fee=\"").append((float)order.getPayfee() / 100).append("\"")
                .append("&notify_url=\"").append(NOTIFY_URL).append("\"")
                .append("&service=\"mobile.securitypay.pay\"")
                .append("&payment_type=\"1\"")
                .append("&_input_charset=\"utf-8\"")
                .append("&it_b_pay=\"30m\"")
                .append("&return_url=\"m.alipay.com\"")
                .toString();

        final String sign = sign(orderinfo, MERCHENT_PRIVATEKEY);

        final String payInfo = orderinfo + "&sign=\"" + sign + "\"&sign_type=\"RSA\"";

        renderJson(new AliPayResponse(payInfo));

    }

    public void callback()throws Exception{

        Map<String,String> params = new HashMap<>();
        Map requestParams = getParaMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数
        //商户订单号
        String out_trade_no = new String(getPara("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //支付宝交易号
        String trade_no = new String(getPara("trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //交易状态
        String trade_status = new String(getPara("trade_status").getBytes("ISO-8859-1"),"UTF-8");

        //如果验证成功
        if(verify(params)){
            logger.debug("结果通知验证成功->" + JSON.toJSONString(params));
            //验证成功时支付宝的返回信息后，就发送“success”给支付宝，避免通知重复发送
            renderText("success");
            if(trade_status.equals("WAIT_BUYER_PAY")) {
                logger.debug("[" + out_trade_no + "]等待买家支付……");
                return;
            }
            if(!ru.lock(RedisUtil.Table.CALLBACK.getKey(out_trade_no), 1800)) {
                logger.debug("订单[" + out_trade_no + "]重复发送支付结果通知");
                return;
            }

            logger.debug("支付结果通知：" + trade_status);
            final boolean isSuccess = trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS");

            Order order = Order.dao.findById(out_trade_no);
            if(order != null) {
                order.setPayflowid(trade_no)
                    .setStatus(isSuccess ? 0 : 1)
                    .setPayresult(String.valueOf(isSuccess ? "00000" : "00701"))//支付宝支付失败
                    .setErrorcode(isSuccess ? "0" : "1")
                    .update();
            } else {
                logger.error("[" + out_trade_no + "]无法在订单库中找到");
                return;
            }

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
            n.setPaytype(1);
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
            msg.setPaytype(IMessageService.Message.PayType.ALIPAY.getValue());
            msg.setServiceid(order.getServicekey());
            msg.setStatus(order.getStatus());
            try {
                ms.notify(msg);
            } catch (Exception e) {
                logger.error("消息通知失败", e);
            }
        } else {
            logger.debug("通知结果验证失败->" + JSON.toJSONString(params));
            renderText("failure");
        }

    }

    private Order createOrder(AlipayRequest pay)throws Exception{

        String consumecode = pay.getConsumecode();
        String cpid = pay.getCpid();
        String channelid = pay.getChannelid();

        if(!cps.checkAuth(consumecode, cpid, channelid)){
            logger.debug("[" + pay.getCporderid() + "]鉴权失败");
            throw new Exception("[" + pay.getCporderid() + "]鉴权失败");
        }

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setOrdertime(pay.getOrdertime());
        //支付方式：08支付宝 09神州付 10银联 11易宝 12百付宝 13 pay19 14积分支付
        o.setEncryptparam("08");
        o.setOrderid_3rd(pay.getCporderid());
        o.setOrdertime(pay.getOrdertime());
        o.setServicekey(pay.getServiceid());

        o.setUseraccount(pay.getIdentityid());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(pay.getSdkversion());
        o.setChannelid(channelid);
        o.setEmpno(pay.getAssistantid());
        o.setCpid(cpid);
        o.setPointid(consumecode);
        o.setUsertype("2");

        UserInfo userInfo = UserInfo.dao.getByCpid(cpid);
        ChargePoint point = ChargePoint.dao.getByConsumecode(consumecode);
        o.setUserindex(userInfo.getUserindex());
        o.setUserid(userInfo.getUserid());
        o.setProductindex(point.getCntindex());
        o.setProductid(point.getProduct().getCntid());
        o.setProductname(point.getProduct().getCntname());
        o.setPointindex(point.getPointindex());
        o.setPointname(point.getPointname());
        if(pay.getFeetype() == 0){
            o.setPayfee(point.getPointvalue());
        }else{
            o.setPayfee(pay.getPayfee());
        }
        o.save();

        return o;
    }

    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static String sign(String content, String privateKey) {

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return URLEncoder.encode(Base64.encode(signed), "UTF-8");
        } catch (Exception e) {
            logger.error("签名异常", e);
        }

        return null;
    }

    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    private static boolean verify(Map<String, String> params) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "true";
        if(params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseTxt = verifyResponse(notify_id);
        }
        String sign = "";
        if(params.get("sign") != null) {sign = params.get("sign");}
        boolean isSign = getSignVeryfy(params, sign);

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
        //AlipayCore.logResult(sWord);

        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    private static boolean getSignVeryfy(Map<String, String> Params, String sign) {
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = paraFilter(Params);
        //获取待签名字符串
        String preSignStr = createLinkString(sParaNew);
        //获得签名验证结果
        return RSA.checkSign(preSignStr, sign, ALI_PUBLICKEY);
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + PARTNER + "&notify_id=" + notify_id;

        return checkUrl(veryfy_url);
    }

    /**
     * 获取远程服务器ATN结果
     * @param urlvalue 指定URL路径地址
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String checkUrl(String urlvalue) {
        String inputLine = "";

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputLine = in.readLine().toString();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }

    static class AliPayResponse extends Response<AliPayResponse> {
        private String payinfo;

        AliPayResponse(String payinfo) {
            this.payinfo = payinfo;
        }

        public String getPayinfo() {
            return payinfo;
        }

        public void setPayinfo(String payinfo) {
            this.payinfo = payinfo;
        }
    }

    static class AlipayRequest extends Request<AlipayRequest>{
        //可选类型
        private  int optype;
        private String cporderid;
        //付款类型 0:金额以计费点定义为准,1:金额以payfee字段为准
        private  int feetype;
        //支付金额 单位为分
        private int payfee;
        private String ordertime;
        private String serviceid;
        private String consumecode;
        private String cpid;
        private String channelid;
        private String identityid;
        private String assistantid;
        private String sdkversion;

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public String getOrdertime() {
            return ordertime;
        }

        public void setOrdertime(String ordertime) {
            this.ordertime = ordertime;
        }

        public String getServiceid() {
            return serviceid;
        }

        public void setServiceid(String serviceid) {
            this.serviceid = serviceid;
        }

        public String getConsumecode() {
            return consumecode;
        }

        public void setConsumecode(String consumecode) {
            this.consumecode = consumecode;
        }

        public String getCpid() {
            return cpid;
        }

        public void setCpid(String cpid) {
            this.cpid = cpid;
        }

        public String getChannelid() {
            return channelid;
        }

        public void setChannelid(String channelid) {
            this.channelid = channelid;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public String getAssistantid() {
            return assistantid;
        }

        public void setAssistantid(String assistantid) {
            this.assistantid = assistantid;
        }

        public String getSdkversion() {
            return sdkversion;
        }

        public void setSdkversion(String sdkversion) {
            this.sdkversion = sdkversion;
        }

        public int getOptype() {
            return optype;
        }

        public void setOptype(int optype) {
            this.optype = optype;
        }

        public int getFeetype() {
            return feetype;
        }

        public void setFeetype(int feetype) {
            this.feetype = feetype;
        }

        public int getPayfee() {
            return payfee;
        }

        public void setPayfee(int payfee) {
            this.payfee = payfee;
        }
    }

    static class AlipayOrderInfo{

        //接口名字，固定值
        private String service;
        //合作者身份ID
        private String partner;
        //参数编码格式，utf-8
        private String _input_charset;
        //签名方式：默认RSA
        private String sign_type;
        //签名
        private String sign;
        //异步通知页面路径
        private String notify_url;
        //客户端编号，可为空
        private String app_id;
        //客户端来源，可为空
        private String appenv;
        //商户网站唯一订单号
        private String out_trade_no;
        //商品名称
        private String subject;
        //支付类型，默认为1，商品购买
        private String payment_type;
        //卖家支付宝账号
        private String seller_id;
        //总金额
        private Number total_fee;
        //商品详情
        private String body;
        //未付款交易的超时时间
        private String it_b_pay;
        //授权令牌
        private String extern_token;
        //使用银行卡支付
        private String paymethod;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String get_input_charset() {
            return _input_charset;
        }

        public void set_input_charset(String _input_charset) {
            this._input_charset = _input_charset;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getAppenv() {
            return appenv;
        }

        public void setAppenv(String appenv) {
            this.appenv = appenv;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getPayment_type() {
            return payment_type;
        }

        public void setPayment_type(String payment_type) {
            this.payment_type = payment_type;
        }

        public String getSeller_id() {
            return seller_id;
        }

        public void setSeller_id(String seller_id) {
            this.seller_id = seller_id;
        }

        public Number getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(Number total_fee) {
            this.total_fee = total_fee;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getIt_b_pay() {
            return it_b_pay;
        }

        public void setIt_b_pay(String it_b_pay) {
            this.it_b_pay = it_b_pay;
        }

        public String getExtern_token() {
            return extern_token;
        }

        public void setExtern_token(String extern_token) {
            this.extern_token = extern_token;
        }

        public String getPaymethod() {
            return paymethod;
        }

        public void setPaymethod(String paymethod) {
            this.paymethod = paymethod;
        }
    }

    @Override
    public <T> T getJSONObject(Class<T> clazz) throws Exception {

        T o = super.getJSONObject(clazz);
        /*
        用来避免支付请求重放问题，将cpid,orderid,time三个属性作为唯一键进行是否已存在确认，默认保存时间为半小时
        */
        if(o instanceof AlipayRequest) {
            AlipayRequest pay = (AlipayRequest) o;
            logger.debug("[" + pay.getCporderid() + "]进行重放校验……");

            String key = RedisUtil.Table.REPLAY.getKey(pay.getCpid() + pay.getCporderid() + pay.getOrdertime());
            logger.debug("重放校验key值：" + key);

            if(ru.getSet(key, "", HOUR) == null) {
                logger.debug("[" + pay.getCporderid() + "]不存在重放异常");
            } else {
                logger.debug("[" + pay.getCporderid() + "]存在重放异常");
                throw new ReplayException("[" + pay.getCporderid() + "]重放异常", pay.toString());
            }

            /*logger.debug("[" + pay.getCporderid() + "]校验客户端发送时间……");
            DateTime ordertime = DateTime.parse(pay.getOrdertime(), DTFORMATTER);
            if(ordertime.plusMinutes(replayDelay).isBeforeNow()) {
                logger.debug("[" + pay.getCporderid() + "]客户端发送时间有超过" + replayDelay + "秒异常");
                throw new ReplayException("客户端时间异常", JSON.toJSONString(pay));
            }
            logger.debug("[" + pay.getCporderid() + "]客户端发送时间无异常");*/
        }

        return o;
    }

    private static final class Base64 {

        private static final int BASELENGTH = 128;
        private static final int LOOKUPLENGTH = 64;
        private static final int TWENTYFOURBITGROUP = 24;
        private static final int EIGHTBIT = 8;
        private static final int SIXTEENBIT = 16;
        private static final int FOURBYTE = 4;
        private static final int SIGN = -128;
        private static char PAD = '=';
        private static byte[] base64Alphabet = new byte[BASELENGTH];
        private static char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

        static {
            for (int i = 0; i < BASELENGTH; ++i) {
                base64Alphabet[i] = -1;
            }
            for (int i = 'Z'; i >= 'A'; i--) {
                base64Alphabet[i] = (byte) (i - 'A');
            }
            for (int i = 'z'; i >= 'a'; i--) {
                base64Alphabet[i] = (byte) (i - 'a' + 26);
            }

            for (int i = '9'; i >= '0'; i--) {
                base64Alphabet[i] = (byte) (i - '0' + 52);
            }

            base64Alphabet['+'] = 62;
            base64Alphabet['/'] = 63;

            for (int i = 0; i <= 25; i++) {
                lookUpBase64Alphabet[i] = (char) ('A' + i);
            }

            for (int i = 26, j = 0; i <= 51; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('a' + j);
            }

            for (int i = 52, j = 0; i <= 61; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('0' + j);
            }
            lookUpBase64Alphabet[62] = (char) '+';
            lookUpBase64Alphabet[63] = (char) '/';

        }

        private static boolean isWhiteSpace(char octect) {
            return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
        }

        private static boolean isPad(char octect) {
            return (octect == PAD);
        }

        private static boolean isData(char octect) {
            return (octect < BASELENGTH && base64Alphabet[octect] != -1);
        }

        /**
         * Encodes hex octects into Base64
         *
         * @param binaryData
         *            Array containing binaryData
         * @return Encoded Base64 array
         */
        public static String encode(byte[] binaryData) {

            if (binaryData == null) {
                return null;
            }

            int lengthDataBits = binaryData.length * EIGHTBIT;
            if (lengthDataBits == 0) {
                return "";
            }

            int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
            int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
            int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1
                    : numberTriplets;
            char encodedData[] = null;

            encodedData = new char[numberQuartet * 4];

            byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

            int encodedIndex = 0;
            int dataIndex = 0;

            for (int i = 0; i < numberTriplets; i++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];

                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                        : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                        : (byte) ((b2) >> 4 ^ 0xf0);
                byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
                        : (byte) ((b3) >> 6 ^ 0xfc);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
            }

            // form integral number of 6-bit groups
            if (fewerThan24bits == EIGHTBIT) {
                b1 = binaryData[dataIndex];
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                        : (byte) ((b1) >> 2 ^ 0xc0);
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4];
                encodedData[encodedIndex++] = PAD;
                encodedData[encodedIndex++] = PAD;
            } else if (fewerThan24bits == SIXTEENBIT) {
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                        : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                        : (byte) ((b2) >> 4 ^ 0xf0);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2];
                encodedData[encodedIndex++] = PAD;
            }

            return new String(encodedData);
        }

        /**
         * Decodes Base64 data into octects
         *
         * @param encoded
         *            string containing Base64 data
         * @return Array containind decoded data.
         */
        public static byte[] decode(String encoded) {

            if (encoded == null) {
                return null;
            }

            char[] base64Data = encoded.toCharArray();
            // remove white spaces
            int len = removeWhiteSpace(base64Data);

            if (len % FOURBYTE != 0) {
                return null;// should be divisible by four
            }

            int numberQuadruple = (len / FOURBYTE);

            if (numberQuadruple == 0) {
                return new byte[0];
            }

            byte decodedData[] = null;
            byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
            char d1 = 0, d2 = 0, d3 = 0, d4 = 0;

            int i = 0;
            int encodedIndex = 0;
            int dataIndex = 0;
            decodedData = new byte[(numberQuadruple) * 3];

            for (; i < numberQuadruple - 1; i++) {

                if (!isData((d1 = base64Data[dataIndex++]))
                        || !isData((d2 = base64Data[dataIndex++]))
                        || !isData((d3 = base64Data[dataIndex++]))
                        || !isData((d4 = base64Data[dataIndex++]))) {
                    return null;
                }// if found "no data" just return null

                b1 = base64Alphabet[d1];
                b2 = base64Alphabet[d2];
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];

                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }

            if (!isData((d1 = base64Data[dataIndex++]))
                    || !isData((d2 = base64Data[dataIndex++]))) {
                return null;// if found "no data" just return null
            }

            b1 = base64Alphabet[d1];
            b2 = base64Alphabet[d2];

            d3 = base64Data[dataIndex++];
            d4 = base64Data[dataIndex++];
            if (!isData((d3)) || !isData((d4))) {// Check if they are PAD characters
                if (isPad(d3) && isPad(d4)) {
                    if ((b2 & 0xf) != 0)// last 4 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 1];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                    return tmp;
                } else if (!isPad(d3) && isPad(d4)) {
                    b3 = base64Alphabet[d3];
                    if ((b3 & 0x3) != 0)// last 2 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 2];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                    tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                    return tmp;
                } else {
                    return null;
                }
            } else { // No PAD e.g 3cQl
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);

            }

            return decodedData;
        }

        /**
         * remove WhiteSpace from MIME containing encoded Base64 data.
         *
         * @param data
         *            the byte array of base64 data (with WS)
         * @return the new length
         */
        private static int removeWhiteSpace(char[] data) {
            if (data == null) {
                return 0;
            }

            // count characters that's not whitespace
            int newSize = 0;
            int len = data.length;
            for (int i = 0; i < len; i++) {
                if (!isWhiteSpace(data[i])) {
                    data[newSize++] = data[i];
                }
            }
            return newSize;
        }
    }
}


