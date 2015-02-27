package com.chinaunicom.unipay.ws.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.unipay.ws.commons.ErrorCode;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.ChargeException;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IChargeService;
import com.chinaunicom.unipay.ws.services.IHttpService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.PayChannel;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.pay19.CipherUtil;
import com.chinaunicom.unipay.ws.utils.pay19.KeyedDigestMD5;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.MINUTE;

/**
 * Created by 兵 on 2014/12/17.
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class ChargeController extends WSController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceBundle resb1 = ResourceBundle.getBundle("payapi");
    private final String URLPAYCHANNEL = "http://change.19ego.cn/channel.jsp";
    private final String URL19PAYBALANCE = "http://change.19ego.cn/query/balance";
    private final String  swift = resb1.getString("payapi.flag");
    private final String  clientid = resb1.getString("payapi.client_id");
    private final String  clientsecret = resb1.getString("payapi.client_secret");
    private final String  shenzhouchargeaddr = resb1.getString("payapi.shenzhou_charge_addr");
    private final String pay19key = resb1.getString("pay19.pay19key");
    private final String notifyurl =resb1.getString("pay19.notifyurl");
    private final String merchantid =resb1.getString("pay19.merchantid");
    private final String versionid =resb1.getString("pay19.versionid");
    private final String USERTYPE = "10";
    private final Integer replayBuffertime = Integer.parseInt(resb1.getString("replay.buffertime"));
    private static final DateTimeFormatter DTFORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    private static long timebuf = 0;
    private static final Prop prop = PropKit.use("payapi.properties", "utf-8");
    private static final Pattern pattern = Pattern.compile(prop.get("pay19.test_serviceid", ""));

    @Resource private IHttpService ihs;
    @Resource
    ICPService cps;
    @Resource
    IMessageService ms;
    @Resource
    IChargeService ics;
    @Resource
    RedisUtil ru;
    //充值卡支付
    public void pay() throws Exception{
        ChargeRequest pay = getJSONObject(ChargeRequest.class);

        IChargeService.Charge charge = getCharge();
        String key = "19pay"+pay.getCardnum1().length()+pay.getCardnum2().length();
        List<PayChannel> list = null;

        if (!ru.exists(key)) {
             list = getChannel(charge.getMerchantid());
             boolean flag = setChannel(list,pay.getCardnum1(),pay.getCardnum2());
            if (!flag){
                throw new ChargeException("卡号或密码不对！");
            }
        }
        charge.setPcid(ru.hget(key, "pcid"));
        charge.setPmid(ru.hget(key, "pmid"));

        //入库交易临时信息
        logger.debug("[CP->" + pay.getCporderid() + "]订单入库……");
        timebuf = System.currentTimeMillis();
        Order order = createOrder(pay);
        String orderid = order.getOrderid();
        logger.debug("[" + orderid + "]订单入库成功，用时：" + (System.currentTimeMillis() - timebuf) + "ms");

        IChargeService.ChargeResponse response = null;

        //充值卡申请支付
        logger.debug("[" + orderid + "]进行充值卡支付申请……");
        timebuf = System.currentTimeMillis();

        charge.setOrderid(orderid);
        charge.setCardnum1(pay.getCardnum1());
        charge.setCardnum2(pay.getCardnum2());
        charge.setSelectamount(pay.getSelect_amount());
        charge.setAmount(String.valueOf(order.getPayfee() * 1.0 / 100));
        response = ics.charge(charge);

        Response res = null;
        if(response.isSucess()){
            logger.debug("[" + orderid + "]收单成功,用时：" + (System.currentTimeMillis() - timebuf) + "ms");;
            res = new Response(0,"支付提交成功！");
            } else {
            logger.debug("[" + orderid + "]收单失败，原因为" + response.getResultstr()+",用时:" + (System.currentTimeMillis() - timebuf) + "ms");
            res = new Response<>(response.toUnipayCode(), ErrorCode.getMsg(ErrorCode.PAY19_ERROR_CODE_, response.getResultstr()));
        }
        renderJson(res);

    }

    //回调
    public void callback() throws Exception{

        renderText("Y");

        final String orderid = getPara("order_id");
        if(!ru.lock(RedisUtil.Table.CALLBACK.getKey(orderid), 30 * MINUTE)) {
            logger.debug("订单[" + orderid + "]重复发送支付结果通知");
            return;
        }

        final String verify = "version_id="+getPara("version_id")+"&merchant_id="+getPara("merchant_id")+"&order_id="+orderid+"&result="+getPara("result")+"&order_date="+getPara("order_date")+"&amount="+getPara("amount")+"&currency="+getPara("currency")+"&pay_sq="+getPara("pay_sq")+"&pay_date="+getPara("pay_date")+"&count="+getPara("count")+"&card_num1="+getPara("card_num1")+"&card_pwd1="+getPara("card_pwd1")+"&pc_id1="+getPara("pc_id1")+"&card_status1="+getPara("card_status1")+"&card_code1="+getPara("card_code1")+"&card_date1="+getPara("card_date1")+"&r1="+getPara("r1")+"&merchant_key="+pay19key;
        final String verifystring = KeyedDigestMD5.getKeyedDigest(verify, "");
        if (!verifystring.equals(getPara("verifystring"))) {
            logger.error("订单[" + orderid + "]签名错误！");
            return;
        }

        logger.debug("支付结果通知:" + verify);
        String cardnum1 = CipherUtil.decryptData(getPara("card_num1"), pay19key);
        String cardpwd1 = CipherUtil.decryptData(getPara("card_pwd1"), pay19key);

        boolean flag = "Y".equals(getPara("result"));

        Order order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(getPara("pay_sq"))
                    .setStatus(flag ? 0 : 1)
                    .setPayresult(flag ? "0" : "1").setErrorcode(getPara("card_code1") );
            order.update();
        } else {
            return;
        }

        if(order.getIswbpay() == 1) {
            if (flag) {
                Map param = new HashMap();
                param.put("amount", order.getPayfee());
                param.put("clientid", clientid);
                param.put("clientsecret", clientsecret);
                param.put("rechargeurl", shenzhouchargeaddr);
                param.put("identityid", order.getUseraccount());
                param.put("serviceid", order.getServicekey());

                logger.debug("[" + shenzhouchargeaddr + "]进行沃币积分充值申请……");
                timebuf = System.currentTimeMillis();
                pointCharge(param);
                logger.debug("[" + shenzhouchargeaddr + "]积分充值申请……用时：" + (System.currentTimeMillis() - timebuf) + "ms");
            }
        }

        if (order.getIswbpay() == 0) {
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
            n.setPaytype(3);
            cps.sendNotification(n);

            //通知消息系统
            IMessageService.Message msg = new IMessageService.Message();
            msg.setCpid(order.getCpid());
            msg.setPayresult(order.getPayresult());
            msg.setOrderid(order.getOrderid());
            msg.setCporderid(order.getOrderid_3rd());
            msg.setPayfee(order.getPayfee());
            msg.setPaytime(order.getPaytime());
            msg.setPaytype(IMessageService.Message.PayType.PAY19.getValue());
            msg.setServiceid(order.getServicekey());
            msg.setStatus(order.getStatus());
            ms.notify(msg);

            if(flag) {
                final Map map = queryBalance("2.00", "275159", getPara("pc_id1"), cardnum1, cardpwd1);
                final String balance = (String) map.get("balance");
                int money = (int) (Float.parseFloat(StringUtils.isEmpty(balance) ? "-1" : balance) * 100);

                final String usertype = order.getUsertype();
                if (money <= 0 || !USERTYPE.equals(usertype)) {
                    return;
                }

                final String cporderid = order.getOrderid_3rd();

                final Order o = new Order();
                o.setOrderid(Tools.getUUID());
                o.setIswbpay(1);
                //支付方式：08支付宝 09神州付 10银联 11易宝 12百付宝 13 pay19 14积分支付
                o.setEncryptparam("13");
                o.setOrderid_3rd(cporderid);
                o.setUseraccount(order.getUseraccount());
                o.setServicekey(order.getServicekey());
                o.setPaytime(Tools.getCurrentTime());
                o.setOrdertime(Tools.getCurrentTime());
                o.setCardno(cardnum1);
                o.setSelectammount(order.getSelectammount() / 100);
                o.setPayfee(pattern.matcher(order.getPointid()).find() ? 2 : money);
                o.save();

                String key = "19pay"+cardnum1.length()+cardpwd1.length();
                String oid = o.getOrderid();
                IChargeService.Charge charge = getCharge();
                charge.setPcid(ru.hget(key, "pcid"));
                charge.setPmid(ru.hget(key, "pmid"));
                charge.setOrderid(oid);
                charge.setCardnum1(cardnum1);
                charge.setCardnum2(cardpwd1);
                charge.setSelectamount(String.valueOf(o.getSelectammount()));
                charge.setAmount(String.valueOf(o.getPayfee() * 1.0 / 100));
                timebuf = System.currentTimeMillis();
                IChargeService.ChargeResponse response = ics.charge(charge);

                if (response.isSucess()) {
                    logger.debug("[" + oid + "]收单成功,用时：" + (System.currentTimeMillis() - timebuf) + "ms");
                } else {
                    logger.debug("[" + oid + "]收单失败，原因为" + ErrorCode.getMsg(ErrorCode.PAY19_ERROR_CODE_, response.getResultstr()) + "用时：" + (System.currentTimeMillis() - timebuf) + "ms");
                }
            }
        }



    }

    private Order createOrder(ChargeRequest pay) throws Exception {

        String consumecode = pay.getConsumecode();
        String cpid = pay.getCpid();
        String channelid = pay.getChannelid();

        Order o = new Order();
        o.setOrderid(Tools.getUUID());
        o.setEncryptparam("13");
        o.setOrderid_3rd(pay.getCporderid());
        o.setOrdertime(pay.getOrdertime());
        o.setServicekey(pay.getServiceid());
        o.setImsi(pay.getImsi());
        o.setUseraccount(pay.getIdentityid());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(pay.getSdkversion());
        o.setChannelid(channelid);
        o.setEmpno(pay.getAssistantid());
        o.setCpid(cpid);
        o.setPointid(consumecode);
        o.setUsertype(pay.getUsertype());
        o.setIswbpay(0);
        o.setSelectammount(new BigDecimal(pay.getSelect_amount()).intValue() * 100);
        o.setCardno(pay.getCardnum1());

        if(cps.checkAuth(consumecode, cpid, channelid)){
            UserInfo userInfo = UserInfo.dao.getByCpid(cpid);
            ChargePoint point = ChargePoint.dao.getByConsumecode(consumecode);
            o.setUserindex(userInfo.getUserindex());
            o.setUserid(userInfo.getUserid());
            o.setProductindex(point.getCntindex());
            o.setProductid(point.getProduct().getCntid());
            o.setProductname(point.getProduct().getCntname());
            o.setPointindex(point.getPointindex());
            o.setPointname(point.getPointname());
            o.setPayfee(pattern.matcher(consumecode).find() ? 2 : point.getPointvalue());
        }
        o.save();

        return o;
    }

    public boolean setChannel(List<PayChannel> list,String cardnum, String cardpwd) {
        int cardlength1 = cardnum.length();
        int cardlength2 = cardpwd.length();
        String key = "19pay" + cardlength1 + cardlength2;
        for(PayChannel payChannel : list){
            if(cardlength1 == 17 && cardlength2 == 18){
                if (payChannel.getPc_desc().equals("全国移动充值卡")){
                    ru.hset(key, "pcid", payChannel.getPc_id(),3600);
                    ru.hset(key, "pmid", payChannel.getPm_id(),3600);
                }
            }else if(cardlength1 == 15 && cardlength2 == 19){
                if (payChannel.getPc_desc().equals("全国联通一卡充")){
                    ru.hset(key, "pcid", payChannel.getPc_id(),3600);
                    ru.hset(key, "pmid", payChannel.getPm_id(),3600);
                }
            }else if(cardlength1 == 19 && cardlength2 == 18){
                if (payChannel.getPc_desc().equals("中国电信充值付费卡")){
                    ru.hset(key, "pcid", payChannel.getPc_id(),3600);
                    ru.hset(key, "pmid", payChannel.getPm_id(),3600);
                }
            }else {
                return false;
            }
        }
        return true;
    }
    public IChargeService.Charge getCharge(){
        IChargeService.Charge charge = new IChargeService.Charge();
        charge.setVersionid(versionid);
        charge.setMerchantid(merchantid);
        charge.setNotifyurl(notifyurl);
        charge.setOrderdate(DTFORMATTER.print(System.currentTimeMillis()));
        charge.setCurrency("RMB");

        return charge;
    }

    public List<PayChannel> getChannel(String merchantid) throws Exception {
        String param = "merchant_id="+merchantid;
        List<PayChannel> list = new ArrayList<>();
        String res = ihs.httpGet(URLPAYCHANNEL, param, "gbk");
        String[] info = StringUtils.trim(res).split("\\|");
        for(int j=0;j<info.length;j+=4) {
            PayChannel payChannel = new PayChannel();
            payChannel.setPc_id(info[j]);
            payChannel.setPm_id(info[j + 1]);
            payChannel.setPc_province(info[j + 2]);
            payChannel.setPc_desc(info[j + 3]);
            list.add(payChannel);
        }
        return list;
    }

    public Map<String, Object> queryBalance(String version,String merchantid,String pcid,String cardnum,String pwd ) throws Exception {
        Map map = new HashMap();
        CipherUtil util = new CipherUtil();
        String cardnum1= util.encryptData(cardnum, pay19key);
        String cardnum2=util.encryptData(pwd, pay19key);

        String verifystring= "version="+version+"&mxid="+merchantid+"&pcid="+pcid+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+
                "&key="+pay19key;	//商户代码源串
        verifystring = KeyedDigestMD5.getKeyedDigest(verifystring, "");

        String param = "version="+version+"&mxid="+merchantid+ "&pcid="+pcid+"&cardnum1="+cardnum1+"&cardnum2="+cardnum2+
                "&verify="+verifystring;

        String res = ihs.httpGet(URL19PAYBALANCE,param,"gbk");
        String result[] = res.split("&");
        for(String str : result){
            String value[] = str.split("=");
            if(value.length == 1) {
                map.put(value[0],"");
                return map;
            }
            map.put(value[0], value[1]);

        }
        return map;
    }
    public String pointCharge(Map<String,Object> map) throws Exception {
        Map headermap = new HashMap();
        headermap.put("client_id",map.get("clientid"));
        headermap.put("client_secret",map.get("clientsecret"));
        headermap.put("Content-Type","application/json;charset=utf-8");

        JSONObject sbody = new JSONObject();
        sbody.put("user_id",map.get("identityid"));
        sbody.put("point_count",map.get("amount"));

        JSONObject datasrc = new JSONObject();
        datasrc.put("service_id",map.get("serviceid"));

        sbody.put("data_src",datasrc);
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("recharge_point_shen_zhou",sbody);

        String res = ihs.httpPost((String)map.get("rechargeurl"),jsonObject.toString(),headermap,"UTF-8");
        logger.info("沃币充值结果："+res);

        return res;
    }

    @Override
    public <T> T getJSONObject(Class<T> clazz) throws Exception {

        T o = super.getJSONObject(clazz);
        /*
        用来避免支付请求重放问题，将cpid,orderid,time三个属性作为唯一键进行是否已存在确认，默认保存时间为半小时
        */
        if(o instanceof ChargeRequest) {
            ChargeRequest pay = (ChargeRequest) o;
            logger.debug("[" + pay.getCporderid() + "]进行重放校验……");

            String key = pay.getCpid() + pay.getCporderid() + pay.getOrdertime();
            logger.debug("重放校验key值：" + key);
            if ((ru.getSet(key, "", replayBuffertime) == null)) {
                logger.debug("[" + pay.getCporderid() + "]不存在重放异常");
            } else {
                logger.debug("[" + pay.getCporderid() + "]存在重放异常");
                throw new ReplayException("[" + pay.getCporderid() + "]重放异常", JSON.toJSONString(pay));
            }

            /*logger.debug("[" + pay.getCporderid() + "]校验客户端发送时间……");
            DateTime ordertime = DateTime.parse(pay.getOrdertime(), DATEFORMATTER);
            if(ordertime.plusMinutes(replayDelay).isBeforeNow()) {
                logger.debug("[" + pay.getCporderid() + "]客户端发送时间有超过" + replayDelay + "秒异常");
                throw new ReplayException("客户端时间异常", JSON.toJSONString(pay));
            }
            logger.debug("[" + pay.getCporderid() + "]客户端发送时间无异常");*/
        }

        return o;
    }
    static class ChargeRequest extends SDKRequest{
        //充值卡号
        private String cardnum1;

        //充值卡密码
        private String cardnum2;

        //充值金额
        private String amount;

        //充值卡面额
        private String select_amount;

        //用户类型
        private String usertype;


        public String getCardnum1() {
            return cardnum1;
        }

        public void setCardnum1(String cardnum1) {
            this.cardnum1 = cardnum1;
        }

        public String getCardnum2() {
            return cardnum2;
        }

        public void setCardnum2(String cardnum2) {
            this.cardnum2 = cardnum2;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getSelect_amount() {
            return select_amount;
        }

        public void setSelect_amount(String select_amount) {
            this.select_amount = select_amount;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }
    }
}
