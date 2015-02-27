package com.chinaunicom.unipay.ws.controllers;

import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.UnipayException;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.HOUR;

/**
 * User: Frank
 * Date: 2015/2/10
 * Time: 16:48
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class TelecomController extends WSController {

    private static final Logger logger = LoggerFactory.getLogger(TelecomController.class);

    private static final Prop prop = PropKit.use("payapi.properties", "utf-8");
    private static final String KEY = prop.get("telecom.key");
    private static final String URL = prop.get("telecom.url");
    private static final String SPID = prop.get("telecom.spid");
    private static final String NOTIFY_URL = prop.get("telecom.notifyurl");
    private static final String CHECK_ORDER_URL = URL + "/api/v1/charge/wo/sms/request_check_code?";
    private static final String CONFIRM_PAY_URL = URL + "/api/v1/charge/wo/sms/confirm_pay?";

    @Resource
    private RedisUtil ru;

    @Resource
    private ICPService cps;

    @Resource
    private IMessageService ms;

    @Resource
    private CloseableHttpClient c;

    static class PayRequest extends Request<PayRequest> {
        private String cporderid;
        private String phone;
        private String ordertime;
        private String serviceid;
        private String consumecode;
        private String cpid;
        private String channelid;
        private String sdkversion;

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public String getSdkversion() {
            return sdkversion;
        }

        public void setSdkversion(String sdkversion) {
            this.sdkversion = sdkversion;
        }
    }
    static class TelecomResponse extends Response<TelecomResponse> {

        public static final TelecomResponse stub = new TelecomResponse();

        public boolean isSuccess() {
            return getCode() == 0;
        }

        private Ext ext;

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }

        public void setText(String text) {
            setMsg(text);
        }

        static class Ext {
            private String correlator;

            public String getCorrelator() {
                return correlator;
            }

            public void setCorrelator(String correlator) {
                this.correlator = correlator;
            }
        }
    }

    static class PayResponse extends Response<PayResponse> {
        private String orderid;

        PayResponse(String orderid) {
            this.orderid = orderid;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }
    }
    public void pay() throws Exception {

        final PayRequest pr = getJSONObject(PayRequest.class);

        final Order order = createOrder(pr);
        final String orderid = order.getOrderid();

        final String request_time = DateTime.now().toString("yyyyMMddHHmmss");
        final String sign_msg = DigestUtils.md5Hex((orderid + NOTIFY_URL + request_time + SPID + order.getPointname() + order.getPayfee() + pr.getPhone() + KEY).getBytes("utf-8"));

        final String url = new StringBuilder(350)
            .append(CHECK_ORDER_URL)
            .append("sp_id=").append(SPID)
            .append("&request_time=").append(request_time)
            .append("&cp_order_id=").append(orderid)
            .append("&total_fee=").append(order.getPayfee())
            .append("&subject=").append(order.getPointname())
            .append("&notify_url=").append(NOTIFY_URL)
            .append("&phone=").append(pr.getPhone())
            .append("&sign_msg=").append(sign_msg)
            .toString();

        CloseableHttpResponse rsp = null;
        try {
            HttpGet get = new HttpGet(url);
            long start = System.currentTimeMillis();
            rsp = c.execute(get);
            final String content = IOUtils.toString(rsp.getEntity().getContent());
            logger.debug("URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms|获取=" + content);

            TelecomResponse tr = TelecomResponse.stub.parse(content);
            if(tr.isSuccess()) {
                order.setPayflowid(tr.getExt().getCorrelator()).update();
                renderJson(new PayResponse(orderid));
            } else {
                throw new UnipayException(tr.getMsg(), orderid);
            }
        } finally {
            if(rsp != null) {
                rsp.close();
            }
        }
    }

    static class ConfirmRequest extends Request<ConfirmRequest> {
        private String orderid;
        private String validatecode;

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getValidatecode() {
            return validatecode;
        }

        public void setValidatecode(String validatecode) {
            this.validatecode = validatecode;
        }
    }
    public void payconfirm() throws Exception {

        final ConfirmRequest cr = getJSONObject(ConfirmRequest.class);

        final Order order = Order.dao.findById(cr.getOrderid());
        if(order == null) {
            throw new Exception("无此订单：" + cr.getOrderid());
        }

        final String request_time = DateTime.now().toString("yyyyMMddHHmmss");
        final String sign_msg = DigestUtils.md5Hex((SPID + request_time + order.getPayflowid() + cr.getValidatecode() + KEY).getBytes("utf-8"));

        final String url = new StringBuilder(1000)
                .append(CONFIRM_PAY_URL)
                .append("sp_id=").append(SPID)
                .append("&request_time=").append(request_time)
                .append("&correlator=").append(order.getPayflowid())
                .append("&check_code=").append(cr.getValidatecode())
                .append("&sign_msg=").append(sign_msg)
                .toString();

        CloseableHttpResponse rsp = null;
        try {
            HttpGet get = new HttpGet(url);
            long start = System.currentTimeMillis();
            rsp = c.execute(get);
            final String content = IOUtils.toString(rsp.getEntity().getContent());
            logger.debug("URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms|获取=" + content);

            TelecomResponse tr = TelecomResponse.stub.parse(content);
            if(tr.isSuccess()) {
                renderJson(new Response());
            } else {
                throw new UnipayException(tr.getMsg(), cr.getOrderid());
            }
        } finally {
            if(rsp != null) {
                rsp.close();
            }
        }

    }

    public void callback() throws Exception {

        final String orderid = getPara("cp_order_id");
        final String correlator = getPara("correlator");
        final String trade_status = getPara("trade_status");
        final String phone = getPara("phone");
        final String response_time = getPara("response_time");
        final int total_fee = getParaToInt("total_fee");
        final String sign_msg = getPara("sign_msg");

        final String verify = DigestUtils.md5Hex((orderid + correlator + trade_status + phone + response_time + total_fee + KEY).getBytes("utf-8"));

        renderText("n");
        if(!verify.equals(sign_msg)) {
            return;
        }

        logger.debug(getRequest().getRequestURI() + getRequest().getQueryString());
        final String key = RedisUtil.Table.CALLBACK.getKey(orderid + correlator);
        if(!ru.lock(key, RedisUtil.HOUR)) {
            logger.debug("重复的支付结果通知:" + key);
            return;
        }

        renderText("y");
        getResponse().setHeader("correlator", correlator);

        Order order = Order.dao.findById(orderid);
        boolean success = trade_status.equals("0");
        if(order != null) {
            order.setStatus(success ? 0 : 1)
                    .setPayresult(success ? "00000" : trade_status)
                    .setErrorcode(success ? "00000" : trade_status)
                    .update();

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
            n.setPaytype(17);
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
            msg.setPaytype(IMessageService.Message.PayType.TELECOM.getValue());
            msg.setServiceid(order.getServicekey());
            msg.setStatus(order.getStatus());
            try {
                ms.notify(msg);
            } catch (Exception e) {
                logger.error("消息通知失败", e);
            }
        } else {
            logger.error("[" + orderid + "]无法在订单库中找到");
        }
    }

    private Order createOrder(PayRequest pay) throws Exception {

        String consumecode = pay.getConsumecode();
        String cpid = pay.getCpid();
        String channelid = pay.getChannelid();

        if(!cps.checkAuth(consumecode, cpid, channelid)){
            logger.debug("[" + pay.getCporderid() + "]鉴权失败");
            throw new Exception("[" + pay.getCporderid() + "]鉴权失败");
        }

        UserInfo userInfo = UserInfo.dao.getByCpid(cpid);
        ChargePoint point = ChargePoint.dao.getByConsumecode(consumecode);

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setEncryptparam("17");
        o.setOrderid_3rd(pay.getCporderid());
        o.setOrdertime(pay.getOrdertime());
        o.setServicekey(pay.getServiceid());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(pay.getSdkversion());
        o.setChannelid(channelid);
        o.setCpid(cpid);
        o.setPointid(consumecode);

        o.setUserindex(userInfo.getUserindex());
        o.setUserid(userInfo.getUserid());
        o.setProductindex(point.getCntindex());
        o.setProductid(point.getProduct().getCntid());
        o.setProductname(point.getProduct().getCntname());
        o.setPointindex(point.getPointindex());
        o.setPointname(point.getPointname());
//        o.setPayfee(point.getPointvalue());
        o.setPayfee(100);

        o.save();

        return o;
    }

    @Override
    public <T> T getJSONObject(Class<T> clazz) throws Exception {

        T o = super.getJSONObject(clazz);
        /*
        用来避免支付请求重放问题，将cpid,orderid,time三个属性作为唯一键进行是否已存在确认，默认保存时间为半小时
        */
        if(o instanceof PayRequest) {
            PayRequest pay = (PayRequest) o;
            logger.debug("[" + pay.getCporderid() + "]进行重放校验……");

            String key = RedisUtil.Table.REPLAY.getKey(pay.getCpid() + pay.getCporderid() + pay.getOrdertime());
            logger.debug("重放校验key值：" + key);

            if(ru.lock(key, HOUR)) {
                logger.debug("[" + pay.getCporderid() + "]不存在重放异常");
            } else {
                logger.debug("[" + pay.getCporderid() + "]存在重放异常");
                throw new ReplayException("[" + pay.getCporderid() + "]重放异常", pay.toJSONString());
            }
        }

        return o;
    }
}
