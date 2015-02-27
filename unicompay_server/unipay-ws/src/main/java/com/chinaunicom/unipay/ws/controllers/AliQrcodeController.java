package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.chinaunicom.unipay.ws.commons.ErrorCode;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.IAliPayService;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.IWeiXinService;
import com.chinaunicom.unipay.ws.services.impl.AliPayService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.MINUTE;

/**
 * Created by Administrator on 2015/1/28 0028.
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class AliQrcodeController extends WSController {

    private final static Logger logger = LoggerFactory.getLogger(BankController.class);
    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");

    private final static String alipay_partner = prop.get("alipay.partner");
    private final static String alipayqrcode_key = prop.get("alipay.key");
    private final static String alipayqrcode_returnurl = prop.get("alipayqrcode.returnurl");
    private final static String alipayqrcode_callbackurl = prop.get("alipayqrcode.callbackurl");
    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private XmlMapper mapper = new XmlMapper();

    @Resource
    IAliPayService ias;
    @Resource
    ICPService ics;
    @Resource
    RedisUtil ru;
    @Resource
    IMessageService ims;
    static class AliQrcodeResponse extends Response<AliQrcodeResponse> {

        private String qrcode;
        private String qrcode_img_url;

        AliQrcodeResponse(){}

        AliQrcodeResponse(int code, String msg, String qrcode, String qrcode_img_url) {
            super(code, msg);
            this.qrcode = qrcode;
            this.qrcode_img_url = qrcode_img_url;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getQrcode_img_url() {
            return qrcode_img_url;
        }

        public void setQrcode_img_url(String qrcode_img_url) {
            this.qrcode_img_url = qrcode_img_url;
        }
    }
    public void getQrcode() throws Exception{

        AliQrcodeRequest req = getJSONObject(AliQrcodeRequest.class);

        String consumecode = req.getConsumecode();

        ChargePoint cp = ChargePoint.dao.getByConsumecode(consumecode);

        IAliPayService.AliPay pay = new IAliPayService.AliPay();

        pay.setPartner(alipay_partner);
        pay.setSign_type("MD5");
        pay.setService("alipay.mobile.qrcode.manage");
        pay.setMethod("add");
        pay.set_input_charset("utf-8");
        pay.setBiz_type("10");
        pay.setTimestamp(DATEFORMATTER.print(System.currentTimeMillis()));

        IAliPayService.AliPay.BizData bizData = new IAliPayService.AliPay.BizData();
        IAliPayService.AliPay.BizData.GoodsInfo goodsInfo = new IAliPayService.AliPay.BizData.GoodsInfo();

        bizData.setMemo("虚拟产品");
        bizData.setNeed_address("F");
        bizData.setTrade_type("1");
        bizData.setReturn_url(alipayqrcode_returnurl);
        bizData.setNotify_url(alipayqrcode_callbackurl);

        goodsInfo.setPrice(String.format("%.2f",cp.getPointvalue()*1.0/100));
        goodsInfo.setId(cp.getPointid());
        goodsInfo.setName(cp.getPointname());

        bizData.setGoods_info(goodsInfo);
        pay.setBiz_data(bizData);

        IAliPayService.AliResponse res = ias.getQrcode(pay);
        Response result = null;
        if (res.isSucess()) {
            if (res.getResponse().getAlipay().isSuccess()) {
                    String aliqrcode = res.getResponse().getAlipay().getQrcode();
                    result = new AliQrcodeResponse(0, "获取二维码成功！", aliqrcode, res.getResponse().getAlipay().getQrcode_img_url());
                    ru.setex(RedisUtil.Table.ALIPAY_QRCODE.getKey(aliqrcode), req.toJSONString(), 5 * MINUTE);
                } else {
                    result = new Response(ErrorCode.getCode(ErrorCode.ALIQRCODE_, res.getResponse().getAlipay().getResult_code()), res.getResponse().getAlipay().getError_message());
                }
            } else {
                    result = new Response(801, res.getError());
        }
        VerifyUtil.logprint("返回数据：",result);
        renderJson(result);
    }

    public void returnNotify() throws Exception{

        String charset = getRequest().getCharacterEncoding();

        Map<String,String> param = new TreeMap();
        for(Map.Entry<String,String[]> entry : getParaMap().entrySet()){
            param.put(URLDecoder.decode(entry.getKey(),charset),URLDecoder.decode(entry.getValue()[0],charset));
        }
        VerifyUtil.logprint("获取数据：",param);
        IAliPayService.NotifyResponse res = ias.returnNotify(param,charset);
        if(res.isSuccess()){

            final String qrcode = ru.getSet(RedisUtil.Table.ALIPAY_QRCODE.getKey(param.get("qrcode")),"");
            if(StringUtils.isEmpty(qrcode)) {
                throw new Exception("二维码无效");
            }
            AliQrcodeRequest aqr = AliQrcodeRequest.stub.parse(qrcode);
            Order o = createOrder(aqr);
            res.setOut_trade_no(o.getOrderid());
        }
        VerifyUtil.logprint("返回数据：",res);
        renderJson(res);
    }

    public void callback() throws Exception{

        final String charset = getRequest().getCharacterEncoding();

        Map<String,String> param = new TreeMap();
        for(Map.Entry<String,String[]> entry : getParaMap().entrySet()){
            param.put(URLDecoder.decode(entry.getKey(),charset),URLDecoder.decode(entry.getValue()[0],charset));
        }
        renderText("success");

        final String sign = param.get("sign");
        final String content = "notify_data=" + param.get("notify_data");
        final String verify = AliPayService.MD5.sign(content, alipayqrcode_key, charset);

        if(!sign.equals(verify)) {
            return;
        }
        VerifyUtil.logprint("回调接口返回数据:",param);
        IAliPayService.NotifyData data = mapper.readValue(param.get("notify_data"),IAliPayService.NotifyData.class);

        String orderid = data.getOut_trade_no();
        if(ru.getSet(RedisUtil.Table.CALLBACK.getKey(orderid), "y", 30 * MINUTE) != null) {
            logger.debug("订单[" + orderid + "]重复发送支付结果通知");
            return;
        }


        Order order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(data.getTrade_no())
                    .setStatus(data.isSuccess() ? 0 : 1)
                    .setPayresult(String.valueOf(data.isSuccess() ? "0" : 1))
                    .setErrorcode(data.isSuccess() ? "0" : "1")
                    .update();
        } else {
            logger.error("[" + orderid + "]无法在订单库中找到");
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
            ics.sendNotification(n);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        //通知消息系统
        IMessageService.Message msg = new IMessageService.Message();
        msg.setCpid(order.getCpid());
        msg.setPayresult(order.getPayresult());
        msg.setOrderid(order.getOrderid());
        msg.setCporderid(order.getOrderid_3rd());
        msg.setPayfee(order.getPayfee());
        msg.setPaytime(order.getPaytime());
        msg.setPaytype(IMessageService.Message.PayType.ALIPAY_QCODE.getValue());
        msg.setServiceid(order.getServicekey());
        msg.setStatus(order.getStatus());
        try {
            ims.notify(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
    private Order createOrder(AliQrcodeRequest aqr) throws Exception {

        String consumecode = aqr.getConsumecode();
        String cpid = aqr.getCpid();
        String channelid = aqr.getChannelid();

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setEncryptparam("15");
        o.setOrderid_3rd(aqr.getCporderid());
        o.setOrdertime(aqr.getOrdertime());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(aqr.getSdkversion());
        o.setServicekey(aqr.getServiceid());
        o.setChannelid(channelid);
        o.setCpid(cpid);
        o.setPointid(consumecode);

        if(ics.checkAuth(consumecode, cpid, channelid)){
            UserInfo userInfo = UserInfo.dao.getByCpid(cpid);
            ChargePoint point = ChargePoint.dao.getByConsumecode(consumecode);
            o.setUserindex(userInfo.getUserindex());
            o.setUserid(userInfo.getUserid());
            o.setProductindex(point.getCntindex());
            o.setProductid(point.getProduct().getCntid());
            o.setProductname(point.getProduct().getCntname());
            o.setPointindex(point.getPointindex());
            o.setPointname(point.getPointname());

            o.setPayfee(point.getPointvalue());

        }
        o.save();

        return o;
    }

    static class AliQrcodeRequest extends SDKRequest<AliQrcodeRequest>{
        public static final AliQrcodeRequest stub = new AliQrcodeRequest();
    }
}
