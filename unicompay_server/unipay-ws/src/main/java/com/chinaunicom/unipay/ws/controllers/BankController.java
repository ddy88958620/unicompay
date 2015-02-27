package com.chinaunicom.unipay.ws.controllers;

import com.chinaunicom.unipay.ws.persistence.BindCard;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.persistence.Quota;
import com.chinaunicom.unipay.ws.persistence.UserInfo;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.services.IBankService;
import com.chinaunicom.unipay.ws.services.ICPService;
import com.chinaunicom.unipay.ws.services.IMessageService;
import com.chinaunicom.unipay.ws.services.UnipayException;
import com.chinaunicom.unipay.ws.services.impl.YibaoService;
import com.chinaunicom.unipay.ws.utils.RedisUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chinaunicom.unipay.ws.services.UnipayException.UN_SUPPORT_PAYTYPE;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.DAY;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.HOUR;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.MINUTE;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.Table;

/**
 * User: Frank
 * Date: 2014/11/6
 * Time: 11:20
 */
@Before({IocInterceptor.class, ExceptionHandler.class, HeaderInterceptor.class})
public class BankController extends WSController {

    private final static Logger logger = LoggerFactory.getLogger(BankController.class);

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");
    private final static List<String> SUPPORT_BANKS = Arrays.asList(prop.get("payapi.supportbanks").split(","));
    private final static List<String> SUPPORT_CREDITS = Arrays.asList(prop.get("payapi.supportcredits").split(","));
    private final static Map<String, IBankService.CardResponse> CARD_INFO = new ConcurrentHashMap<>(2500);

    static {
        try {
            LineIterator iterator = IOUtils.lineIterator(Thread.currentThread().getContextClassLoader().getResourceAsStream("cardinfo.txt"), "utf-8");
            String[] buf; IBankService.CardResponse card;
            while(iterator.hasNext()) {
                buf = iterator.nextLine().split(";");
                if(buf.length == 3) {
                    card = new IBankService.CardResponse();
                    card.setBankname(buf[0]);
                    card.setCardtype(buf[2].equals("借记卡") ? 1 : (buf[2].equals("贷记卡") ? 2 : 0));
                    card.setIsvalid(1);
                    CARD_INFO.put(buf[1], card);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Resource RedisUtil ru;
    @Resource IBankService bs;
    @Resource ICPService cps;
    @Resource IMessageService ms;

    static class PrepayRequest extends Request<PrepayRequest> {
        public final static PrepayRequest stub = new PrepayRequest();

        private String cporderid;
        private String ordertime;
        private String serviceid;
        private String consumecode;
        private String cpid;
        private String channelid;

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
    }
    static class PrepayResponse extends Response<PrepayResponse> {
        public final static PrepayResponse stub = new PrepayResponse();

        private String preorderid;

        PrepayResponse(){}

        PrepayResponse(String preorderid) {
            this.preorderid = preorderid;
        }

        public String getPreorderid() {
            return preorderid;
        }

        public void setPreorderid(String preorderid) {
            this.preorderid = preorderid;
        }
    }
    public void prepay() throws Exception{

        final PrepayRequest pr = getJSONObject(PrepayRequest.class);

        final String preorderid = pr.getCpid() + pr.getCporderid();
        ru.setnx(Table.PREORDER.getKey(preorderid), pr.toJSONString(), 5 * MINUTE);

        renderJson(new PrepayResponse(preorderid));
    }

    static class VaCodeResponse extends Response<VaCodeResponse> {

        private String orderid;

        public VaCodeResponse(int code, String msg, String orderid) {
            super(code, msg);
            this.orderid = orderid;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }
    }
    static class PayRequest extends Request<PayRequest> {

        public static final PayRequest stub = new PayRequest();

        public final static int CREDIT = 0;
        public final static int DEBIT = 1;
        public final static int BIND = 2;

        private String preorderid;

        //0：信用卡支付；1：储蓄卡支付；2：绑卡支付
        private Integer paytype;

        //信用卡或储蓄卡卡号
        private String cardno;

        //信用卡有效期，格式：月月年年，例如：0715
        private String validthru;

        //信用卡背后的3位数字
        private String cvv2;

        //01：身份证；暂只支持01 身份证
        private String idcardtype;

        //证件号
        private String idcard;

        //持卡人姓名
        private String owner;

        //借记卡柜面或信用卡银行预留手机号
        private String phone;

        //绑卡ID
        private String bindid;

        //CP生成的唯一订单号，最长50位
        private String cporderid;

        //CP订单生成时间
        private String ordertime;

        //服务标示
        private String serviceid;

        //沃商店计费点
        private String consumecode;

        //cpID
        private String cpid;

        //渠道ID
        private String channelid;

        //最长50位，商户生成的用户唯一标识
        private String identityid;

        //IMSI
        private String imsi;

        //0 ：IMEI；1：MAC；2：UUID（针对IOS系统）；3：OTHER
        private Integer terminaltype;

        //最长50位
        private String terminalid;

        //3G加油站营业员编号
        private String assistantid;

        //SDK版本号
        private String sdkversion;

        private int feetype;

        private int payfee;

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

        public Integer getPaytype() {
            return paytype;
        }

        public void setPaytype(Integer paytype) {
            this.paytype = paytype;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBindid() {
            return bindid;
        }

        public void setBindid(String bindid) {
            this.bindid = bindid;
        }

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public String getServiceid() {
            return serviceid;
        }

        public void setServiceid(String serviceid) {
            this.serviceid = serviceid;
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

        public Integer getTerminaltype() {
            return terminaltype;
        }

        public void setTerminaltype(Integer terminaltype) {
            this.terminaltype = terminaltype;
        }

        public String getTerminalid() {
            return terminalid;
        }

        public void setTerminalid(String terminalid) {
            this.terminalid = terminalid;
        }

        public String getConsumecode() {
            return consumecode;
        }

        public void setConsumecode(String consumecode) {
            this.consumecode = consumecode;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
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

        public String getOrdertime() {
            return ordertime;
        }

        public void setOrdertime(String ordertime) {
            this.ordertime = ordertime;
        }

        public boolean isDebitpay() {
            return (paytype == 1);
        }

        public boolean isCreditpay() {
            return (paytype == 0);
        }

        public boolean isBindpay() {
            return (paytype == BIND);
        }

        public String getPreorderid() {
            return preorderid;
        }

        public void setPreorderid(String preorderid) {
            this.preorderid = preorderid;
        }

        public boolean isPreorder() {
            return StringUtils.isNotEmpty(preorderid);
        }
    }
    public void pay() throws Exception {

        final PayRequest prq = getJSONObject(PayRequest.class);

        if(prq.isPreorder()) {
            String buf = ru.getSet(Table.PREORDER.getKey(prq.getPreorderid()), "");
            if(StringUtils.isEmpty(buf)) {
                throw new Exception("预支付订单号不存在或已过期");
            } else {
                PrepayRequest prr = PrepayRequest.stub.parse(buf);
                prq.setConsumecode(prr.getConsumecode());
                prq.setCpid(prr.getCpid());
                prq.setCporderid(prr.getCporderid());
                prq.setOrdertime(prr.getOrdertime());
                prq.setServiceid(prr.getServiceid());
                prq.setChannelid(prr.getChannelid());
            }
        }

        Order order = createOrder(prq);
        String orderid = order.getOrderid();

        IBankService.Pay bsPay;
        if(prq.isBindpay()) {
            bsPay = new YibaoService.BindPay(prq.getBindid());
        } else if(prq.isCreditpay()) {
            bsPay = new YibaoService.CreditPay(prq.getValidthru(), prq.getCvv2(), prq.getCardno(), prq.getPhone());
        } else if(prq.isDebitpay()) {
            bsPay = new YibaoService.DebitPay(prq.getIdcard(), prq.getOwner(), prq.getCardno(), prq.getPhone());
        } else {
            logger.debug("[" + orderid + "]支付类型" + prq.getPaytype() + "不支持");
            order.setStatus(1).setErrorcode(UN_SUPPORT_PAYTYPE).setPayresult(UN_SUPPORT_PAYTYPE).update();
            throw new UnipayException(UN_SUPPORT_PAYTYPE, "支付类型为空或不支持", orderid);
        }
        bsPay.setOrderid(orderid);
        bsPay.setAmount(order.getPayfee());
        bsPay.setIdentityid(order.getUseraccount());
        bsPay.setTranstime((int) (System.currentTimeMillis() / 1000));
        bsPay.setProductname(order.getProductname() + "-" + order.getPointname());
        bsPay.setTerminalid(prq.getTerminalid());
        bsPay.setTerminaltype(prq.getTerminaltype());
        String realIP = getRequest().getHeader("X-Real-IP");
        bsPay.setUserip(StringUtils.isEmpty(realIP) ? getRequest().getRemoteAddr() : realIP);

        IBankService.PayResponse pr = bs.pay(bsPay);
        Response rsp;
        if(pr.isSuccess()){
            if(!pr.needConfirm() && prq.isBindpay()){
                IBankService.ConfirmResponse cr = bs.confirm(pr.getOrderid(), null);
                if(cr.isSuccess()) {
                    rsp = new Response();
                } else {
                    order.setPayresult(String.valueOf(cr.toUnipayCode())).setErrorcode(cr.getCode()).setStatus(1).update();
                    rsp = new Response(cr.toUnipayCode(), cr.getMsg());
                }
            } else {
                IBankService.VaCodeResponse vcr = bs.sendVaCode(pr.getOrderid());
                if(vcr.isSuccess()) {
                    rsp = new VaCodeResponse(1, "申请支付成功,并且需要短信确认", pr.getOrderid());
                } else {
                    order.setPayresult(String.valueOf(vcr.toUnipayCode())).setErrorcode(vcr.getCode()).setStatus(1).update();
                    rsp = new Response(vcr.toUnipayCode(), vcr.getMsg());
                }
            }
        } else {
            order.setPayresult(String.valueOf(pr.toUnipayCode())).setErrorcode(pr.getCode()).setStatus(1).update();
            throw new UnipayException(String.valueOf(pr.toUnipayCode()), pr.getMsg(), pr.getOrderid());
        }

        ru.setex(Table.ORDER.getKey(orderid), prq.toJSONString(), 30 * MINUTE);

        renderJson(rsp);
    }

    public void validatecode() throws Exception {

        final ConfirmRequest confirm = getJSONObject(ConfirmRequest.class);
        final IBankService.VaCodeResponse vcr = bs.sendVaCode(confirm.getOrderid());

        renderJson((vcr.isSuccess() ? new Response() : new Response(vcr.toUnipayCode(), vcr.getMsg())));
    }

    static class ConfirmRequest extends Request {

        //计费服务器入库订单号，最长50位
        private String orderid;
        //短信确认验证码
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

        final ConfirmRequest confirm = getJSONObject(ConfirmRequest.class);
        final String orderid = confirm.getOrderid();
        final String validatecode = confirm.getValidatecode();
        if(StringUtils.isEmpty(orderid)) {
            logger.debug("订单号为空！");
            renderJson(new Response(399, "订单号为空！"));
            return;
        }

        final String id = orderid + (StringUtils.isNotEmpty(validatecode) ? validatecode : "");
        final String lockKey = Table.CONFIRM_LOCK.getKey(id);
        if(ru.lock(lockKey, 10000L)) {
            try {
                final String key = Table.CONFIRM.getKey(id);
                String content = ru.get(key);
                if (StringUtils.isNotEmpty(content)) {
                    renderJson(content);
                    return;
                }

                final IBankService.ConfirmResponse cr = bs.confirm(confirm.getOrderid(), confirm.getValidatecode());
                Response rsp = (cr.isSuccess() ? new Response() : new Response(cr.toUnipayCode(), cr.getMsg()));

                content = rsp.toJSONString();
                ru.setex(key, content, 10 * MINUTE);

                renderJson(content);
            } finally {
                ru.unlock(lockKey);
            }
        } else {
            renderJson(new Response(399, "相同订单已提交支付确认！"));
        }
    }

    static class IdentityRequest extends Request {

        private String identityid;

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }
    }
    static class BindCardsResponse extends Response<BindCardsResponse> {

        private List<BindCard> cardlist = new ArrayList<>();

        public List<BindCard> getCardlist() {
            return cardlist;
        }

        public void add(BindCard card) {
            this.cardlist.add(card);
        }

        static class BindCard {
            private String bindid;
            private String cardno;
            private String cardname;
            private String cardtop;
            private String cardlast;
            private int selected;

            BindCard(String bindid, String cardno, String cardname, String cardtop, String cardlast, int selected) {
                this.bindid = bindid;
                this.cardno = cardno;
                this.cardname = cardname;
                this.cardtop = cardtop;
                this.cardlast = cardlast;
                this.selected = selected;
            }

            public String getBindid() {
                return bindid;
            }

            public void setBindid(String bindid) {
                this.bindid = bindid;
            }

            public String getCardno() {
                return cardno;
            }

            public void setCardno(String cardno) {
                this.cardno = cardno;
            }

            public String getCardname() {
                return cardname;
            }

            public void setCardname(String cardname) {
                this.cardname = cardname;
            }

            public String getCardtop() {
                return cardtop;
            }

            public void setCardtop(String cardtop) {
                this.cardtop = cardtop;
            }

            public String getCardlast() {
                return cardlast;
            }

            public void setCardlast(String cardlast) {
                this.cardlast = cardlast;
            }

            public int getSelected() {
                return selected;
            }

            public void setSelected(int selected) {
                this.selected = selected;
            }
        }
    }
    public void bindlist() throws Exception {

        final String identityid = getJSONObject(IdentityRequest.class).getIdentityid();
        if(StringUtils.isEmpty(identityid)) {
            logger.debug("账户唯一标识为空！");
            renderJson(new Response(399, "账户唯一标识为空！"));
            return;
        }

        final String key = Table.BINDCARD.getKey(identityid);
        String content = ru.get(key);
        if(StringUtils.isNotEmpty(content)) {
            logger.debug("从缓存中获取绑卡信息->" + content);
            renderJson(content);
            return;
        }

        final BindCardsResponse bcr = new BindCardsResponse();
        for (BindCard bc : BindCard.dao.getByIdentityId(identityid)) {
            bcr.add(new BindCardsResponse.BindCard(bc.getCardid(), bc.getCardno(), bc.getCardname(), bc.getCardtop(), bc.getCardlast(), bc.getIsdefault()));
        }

        content = bcr.toJSONString();
        logger.debug("绑卡信息缓存->" + content);
        ru.set(key, content);

        renderJson(content);
    }

    static class BindcardOp extends Request<BindcardOp> {
        //操作类型 0：解绑；1：设为默认
        private int optype;
        //用户唯一标识
        private String identityid;
        //绑卡ID
        private String bindid;

        public boolean isUnbind() {
            return optype == 0;
        }

        public boolean isSetdefault() {
            return optype == 1;
        }

        public int getOptype() {
            return optype;
        }

        public void setOptype(int optype) {
            this.optype = optype;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public String getBindid() {
            return bindid;
        }

        public void setBindid(String bindid) {
            this.bindid = bindid;
        }
    }
    public void bindset() throws Exception {

        final BindcardOp bcop = getJSONObject(BindcardOp.class);
        final String key = Table.BINDOPT.getKey(bcop.getBindid());

        Response rsp = new Response();
        if(ru.lock(key)){
            try {
                if (bcop.isUnbind()) {
                    IBankService.UnbindResponse ubr = bs.unbindCard(bcop.getBindid(), bcop.getIdentityid());
                    if (!ubr.isSuccess()) {
                        rsp = new Response(ubr.toUnipayCode(), ubr.getMsg());
                    } else {
                        BindCard.dao.deleteById(bcop.getBindid());
                    }
                }

                if (bcop.isSetdefault()) {
                    BindCard.dao.setDefault(bcop.getIdentityid(), bcop.getBindid());
                }
            } finally {
                ru.del(Table.BINDCARD.getKey(bcop.getIdentityid()));
                ru.unlock(key);
            }
        } else {
            logger.debug("当前绑定卡正处于设置状态！");
            rsp = new Response(399, "当前绑定卡正处于设置状态！");
        }

        renderJson(rsp);
    }

    static class CardRequest extends Request<CardRequest> {
        private String cardno;

        public boolean isPrefix() {
            return cardno == null ? false : cardno.length() == 6;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }
    }
    static class CardResponse extends Response<CardResponse> {
        private String bankname;
        private String cardno;
        //1：储蓄卡 2：信用卡
        private int cardtype;
        //0：无效卡号 1：有效的银行卡号
        private int isvalid;

        CardResponse(String bankname, String cardno, int cardtype, int isvalid) {
            this.bankname = bankname;
            this.cardno = cardno;
            this.cardtype = cardtype;
            this.isvalid = isvalid;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public int getCardtype() {
            return cardtype;
        }

        public void setCardtype(int cardtype) {
            this.cardtype = cardtype;
        }

        public int getIsvalid() {
            return isvalid;
        }

        public void setIsvalid(int isvalid) {
            this.isvalid = isvalid;
        }
    }
    public void cardinfo() throws Exception {

        final CardRequest card = getJSONObject(CardRequest.class);
        if(StringUtils.isEmpty(card.getCardno())) {
            logger.debug("卡号为空！");
            renderJson(new Response(399, "卡号为空！"));
            return;
        }

        final String cardno = card.getCardno();
        final String key = Table.CARDINFO.getKey(cardno);
        final String cache = ru.get(key);
        if(StringUtils.isNotEmpty(cache)) {
            logger.debug("从缓存中获取卡信息->" + cache);
            renderJson(cache);
            return;
        }

        final IBankService.CardResponse c = (card.isPrefix() ? CARD_INFO.get(cardno) : bs.cardinfo(cardno));
        final Response cr;
        if(c != null) {
            if ((c.isDebitCard() && !SUPPORT_BANKS.contains(c.getBankname()))
                    || (c.isCreditCard() && !SUPPORT_CREDITS.contains(c.getBankname()))) {
                c.setIsvalid(0);
            }

            if (c.isSuccess()) {
                String data = c.toJSONString();
                logger.debug("卡信息缓存->" + data);
                ru.setex(key, data, DAY);
                cr = new CardResponse(c.getBankname(), c.getCardno(), c.getCardtype(), c.getIsvalid());
            } else {
                cr = new Response(c.toUnipayCode(), c.getMsg());
            }
        } else {
            cr = new CardResponse(null, null, 0, 0);
        }

        renderJson(cr);
    }

    public void callback() throws Exception {

        String data = getPara("data");
        String key = getPara("encryptkey");

        IBankService.CallbackResponse cbr = bs.callback(data, key);
        logger.debug("订单[" + cbr.getOrderid() + "]银行卡支付返回码：" +
                cbr.getCode() + ",返回结果：" + cbr.getMsg() + ",订单状态：" + cbr.getStatus());
        renderText("通知成功");

        String orderid = cbr.getOrderid();
        if(ru.getSet(Table.CALLBACK.getKey(orderid), "y", 30 * MINUTE) != null) {
            logger.debug("订单[" + orderid + "]重复发送支付结果通知");
            return;
        }

        Order order = Order.dao.findById(orderid);
        if(order != null) {
            order.setPayflowid(cbr.getYborderid())
                    .setStatus(cbr.isSuccess() ? 0 : 1)
                    .setPayresult(cbr.isSuccess() ? "00000" : "00" + cbr.toUnipayCode())
                    .setErrorcode(cbr.isSuccess() ? "00000" : cbr.getCode())
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
        n.setPaytype(5);
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
        msg.setPaytype(IMessageService.Message.PayType.YIBAO.getValue());
        msg.setServiceid(order.getServicekey());
        msg.setStatus(order.getStatus());
        try {
            ms.notify(msg);
        } catch (Exception e) {
            logger.error("消息通知失败", e);
        }

        final String paydata = ru.get(Table.ORDER.getKey(orderid));
        PayRequest pr = null;
        if(StringUtils.isNotEmpty(paydata)) {
            pr = PayRequest.stub.parse(paydata);
        }
        if(cbr.isSuccess() && (StringUtils.isEmpty(paydata) || !pr.isBindpay())) {
            final String bindid = cbr.getBindid();
            BindCard bindCard = BindCard.dao.findById(bindid);
            if(bindCard == null) {
                IBankService.BindCardResponse bcl = bs.bindcards(cbr.getIdentityid());
                IBankService.BindCardResponse.BindCard bc = bcl.getBindCard(bindid);
                if(bc != null) {
                    bindCard = new BindCard();
                    bindCard.setCardno(pr != null ? pr.getCardno() : (bc.getCard_top() + "*******" + bc.getCard_last()))
                            .setBindvalidthru(bc.getBindvalidthru())
                            .setCardid(bindid)
                            .setCardlast(bc.getCard_last())
                            .setCardtop(bc.getCard_top())
                            .setCardname(bc.getCard_name())
                            .setIdentityId(cbr.getIdentityid())
                            .setIsDefault(bcl.getCardlist().size() == 1 ? 1 : 0)//如果是第一张卡则直接设置成默认卡
                            .save();
                }
            }
            ru.del(Table.BINDCARD.getKey(cbr.getIdentityid()));
        }
    }

    static class SupportResponse extends Response<SupportResponse> {
        public final static SupportResponse stub = new SupportResponse();

        private List<String> banks;
        private List<String> credits;

        SupportResponse() {}

        SupportResponse(List<String> banks, List<String> credits) {
            this.banks = banks;
            this.credits = credits;
        }

        public List<String> getBanks() {
            return banks;
        }

        public void setBanks(List<String> banks) {
            this.banks = banks;
        }

        public List<String> getCredits() {
            return credits;
        }

        public void setCredits(List<String> credits) {
            this.credits = credits;
        }
    }
    public void supports() throws Exception {

        String sr = ru.get("supports");
        if(StringUtils.isEmpty(sr)) {
            logger.debug("生成支持卡列表。");
            sr = new SupportResponse(SUPPORT_BANKS, SUPPORT_CREDITS).toJSONString();
            ru.setnx("supports", sr);
            logger.debug("支持卡列表缓存。");
        } else {
            logger.debug("从缓存中获取卡列表。");
        }

        renderJson(sr);
    }

    static class QuotaResponse extends Response<QuotaResponse> {
        private String cardno;
        private String identityid;
        private int oncefee;
        private int dayfee;
        private int monthfee;

        QuotaResponse(String cardno, String identityid, int oncefee, int dayfee, int monthfee) {
            this.cardno = cardno;
            this.identityid = identityid;
            this.oncefee = oncefee;
            this.dayfee = dayfee;
            this.monthfee = monthfee;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public int getOncefee() {
            return oncefee;
        }

        public void setOncefee(int oncefee) {
            this.oncefee = oncefee;
        }

        public int getDayfee() {
            return dayfee;
        }

        public void setDayfee(int dayfee) {
            this.dayfee = dayfee;
        }

        public int getMonthfee() {
            return monthfee;
        }

        public void setMonthfee(int monthfee) {
            this.monthfee = monthfee;
        }
    }
    public void quotainfo() throws Exception{

        final QuotaCardInfo qci = getJSONObject(QuotaCardInfo.class);

        Response rsp = new Response();
        int cardnoexists = 0;
        List<Quota> quotas = Quota.dao.getQuotaByAccount(qci.getIdentityid());
        for (Quota q : quotas){
            if(q.getCardno().equals(qci.getCardno())){
                cardnoexists = 1;
                rsp = new QuotaResponse(q.getCardno(), q.getUseraccount(), q.getOncefee(), q.getDayfee(), q.getMonthfee());
                break;
            }
        }
        if(cardnoexists == 0){
            logger.debug("帐号[" + qci.getIdentityid() + "]未查询到银行卡[" + qci.getCardno() + "]限额信息");
            rsp = new Response(304, "帐号["+qci.getIdentityid()+"]未查询到银行卡["+qci.getCardno()+"]限额信息");
        }

        renderJson(rsp);
    }

    public void quotaset() throws Exception{

        final QuotaInfo qi = getJSONObject(QuotaInfo.class);

        Quota quota = Quota.dao.getQuotaByCardno(qi.getIdentityid(), qi.getCardno());
        if(null == quota){
            quota = new Quota();
            quota.setUseraccount(qi.getIdentityid());
            quota.setCardno(qi.getCardno());
            quota.setOncefee(qi.getOncefee());
            quota.setDayfee(qi.getDayfee());
            quota.setMonthfee(qi.getMonthfee());
            quota.setChangetime(Tools.getCurrentTime());
            quota.save();
            logger.debug("帐号[" + qi.getIdentityid() + "]设置银行卡[" + qi.getCardno() + "]限额");
        } else {
            quota.setOncefee(qi.getOncefee());
            quota.setDayfee(qi.getDayfee());
            quota.setMonthfee(qi.getMonthfee());
            quota.setChangetime(Tools.getCurrentTime());
            quota.update();
            logger.debug("帐号["+qi.getIdentityid()+"]修改银行卡["+qi.getCardno()+"]限额");
        }

        renderJson(new Response());
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

            String key = Table.REPLAY.getKey(pay.getCpid() + pay.getCporderid() + pay.getOrdertime());
            logger.debug("重放校验key值：" + key);

            if(ru.getSet(key, "", HOUR) == null) {
                logger.debug("[" + pay.getCporderid() + "]不存在重放异常");
            } else {
                logger.debug("[" + pay.getCporderid() + "]存在重放异常");
                throw new ReplayException("[" + pay.getCporderid() + "]重放异常", pay.toJSONString());
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
        o.setEncryptparam("11");
        o.setOrderid_3rd(pay.getCporderid());
        o.setOrdertime(pay.getOrdertime());
        o.setServicekey(pay.getServiceid());
        o.setImsi(pay.getImsi());
        o.setUseraccount(StringUtils.isEmpty(pay.getIdentityid()) ? pay.getCardno() : pay.getIdentityid());
        o.setPaytime(Tools.getCurrentTime());
        o.setSdkversion(pay.getSdkversion());
        o.setChannelid(channelid);
        o.setEmpno(pay.getAssistantid());
        o.setCpid(cpid);
        o.setPointid(consumecode);
        o.setCardno(pay.isBindpay() ? pay.getBindid() : pay.getCardno());
        o.setUsertype("2");

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

    static class QuotaCardInfo extends Request {

        private String identityid;
        private String cardno;

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }
    }

    static class QuotaInfo extends QuotaCardInfo{
        //单次限额
        private int oncefee;
        //单日限额
        private int dayfee;
        //单月限额
        private int monthfee;

        public int getOncefee() {
            return oncefee;
        }

        public void setOncefee(int oncefee) {
            this.oncefee = oncefee;
        }

        public int getDayfee() {
            return dayfee;
        }

        public void setDayfee(int dayfee) {
            this.dayfee = dayfee;
        }

        public int getMonthfee() {
            return monthfee;
        }

        public void setMonthfee(int monthfee) {
            this.monthfee = monthfee;
        }

    }
}
