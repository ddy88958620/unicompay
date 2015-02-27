package com.chinaunicom.unipay.ws.services;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.ResourceBundle;

/**
 * User: Frank
 * Date: 2015/1/8
 * Time: 9:37
 */
public interface IBankService {

    public PayResponse pay(Pay pay) throws Exception;
    public VaCodeResponse sendVaCode(String orderid) throws Exception;
    public ConfirmResponse confirm(String orderid, String vaCode) throws Exception;
    public UnbindResponse unbindCard(String bindid, String identityid) throws Exception;
    public CardResponse cardinfo(String cardno) throws Exception;
    public BindCardResponse bindcards(String identityid) throws Exception;
    public CallbackResponse callback(String data, String key) throws Exception;

    public static class Response {

        protected String code;
        protected String msg;

        public String toJSONString() {
            return JSON.toJSONString(this);
        }

        public void setError_code(String code) {
            this.code = code;
        }

        public void setError_msg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess() {
            return StringUtils.isEmpty(code);
        }

        public int toUnipayCode() {
            if(isSuccess()) {
                return 0;
            } else {
                return (Integer.parseInt(code) % 600000 + 400);
            }
        }

        public String getCode(){ return code;}
        public String getMsg() { return msg;}
    }

    public static class UnbindResponse extends Response {
        private String merchantaccount;
        private String bindid;
        private String identityid;
        private int identitytype;

        public String getMerchantaccount() {
            return merchantaccount;
        }

        public void setMerchantaccount(String merchantaccount) {
            this.merchantaccount = merchantaccount;
        }

        public String getBindid() {
            return bindid;
        }

        public void setBindid(String bindid) {
            this.bindid = bindid;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public int getIdentitytype() {
            return identitytype;
        }

        public void setIdentitytype(int identitytype) {
            this.identitytype = identitytype;
        }
    }

    public static class BindCardResponse extends Response {
        private String merchantaccount;
        private String identityid;
        private int identitytype;
        List<BindCard> cardlist;

        public BindCard getBindCard(String bindid) {
            if(cardlist == null || cardlist.isEmpty())
                return null;

            for(BindCard bindCard : cardlist) {
                if(bindCard.bindid.equals(bindid)) {
                    return bindCard;
                }
            }
            return null;
        }

        public static class BindCard {
            private String bindid;
            private String card_top;
            private String card_last;
            private String card_name;
            private int bindvalidthru;
            private String phone;

            public String getBindid() {
                return bindid;
            }

            public void setBindid(String bindid) {
                this.bindid = bindid;
            }

            public String getCard_top() {
                return card_top;
            }

            public void setCard_top(String card_top) {
                this.card_top = card_top;
            }

            public String getCard_last() {
                return card_last;
            }

            public void setCard_last(String card_last) {
                this.card_last = card_last;
            }

            public String getCard_name() {
                return card_name;
            }

            public void setCard_name(String card_name) {
                this.card_name = card_name;
            }

            public int getBindvalidthru() {
                return bindvalidthru;
            }

            public void setBindvalidthru(int bindvalidthru) {
                this.bindvalidthru = bindvalidthru;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

        public String getMerchantaccount() {
            return merchantaccount;
        }

        public void setMerchantaccount(String merchantaccount) {
            this.merchantaccount = merchantaccount;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public int getIdentitytype() {
            return identitytype;
        }

        public void setIdentitytype(int identitytype) {
            this.identitytype = identitytype;
        }

        public List<BindCard> getCardlist() {
            return cardlist;
        }

        public void setCardlist(List<BindCard> cardlist) {
            this.cardlist = cardlist;
        }
    }

    public static class CallbackResponse extends Response {

        private final static int SUCCESS = 1;
        private final static int FAILURE = 0;

        private int amount;
        //银行名称
        private String bank;
        private String bindid;
        private int bindvalidthru;
        private String identityid;
        private int identitytype;
        //卡号后四位
        private String lastno;
        //易宝交易流水号
        private String yborderid;
        private String orderid;
        //0：失败 1：成功 2：未处理 3：处理中
        private int status;

        public void setErrorcode(String code) {
            setError_code(code);
        }
        public void setErrormsg(String msg) {
            setError_msg(msg);
        }

        public boolean isSuccess() {
            return status == SUCCESS;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBindid() {
            return bindid;
        }

        public void setBindid(String bindid) {
            this.bindid = bindid;
        }

        public int getBindvalidthru() {
            return bindvalidthru;
        }

        public void setBindvalidthru(int bindvalidthru) {
            this.bindvalidthru = bindvalidthru;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public int getIdentitytype() {
            return identitytype;
        }

        public void setIdentitytype(int identitytype) {
            this.identitytype = identitytype;
        }

        public String getLastno() {
            return lastno;
        }

        public void setLastno(String lastno) {
            this.lastno = lastno;
        }

        public String getYborderid() {
            return yborderid;
        }

        public void setYborderid(String yborderid) {
            this.yborderid = yborderid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }
    }

    public static class CardResponse extends Response {
        private String bankname;
        private String cardno;
        //1：储蓄卡 2：信用卡
        private int cardtype;
        //0：无效卡号 1：有效的银行卡号
        private int isvalid;

        public boolean isCreditCard() {
            return cardtype == 2;
        }
        public boolean isDebitCard() {
            return cardtype == 1;
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

    public static class VaCodeResponse extends Response {

        private String merchantaccount;
        private String orderid;
        private String phone;
        private int sendtime;

        public String getMerchantaccount() {
            return merchantaccount;
        }

        public void setMerchantaccount(String merchantaccount) {
            this.merchantaccount = merchantaccount;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getSendtime() {
            return sendtime;
        }

        public void setSendtime(int sendtime) {
            this.sendtime = sendtime;
        }
    }

    public static class ConfirmResponse extends Response {

        private String merchantaccount;
        private String orderid;
        //易宝交易流水号
        private String yborderid;
        //银行卡类型 1：储蓄卡 2：信用卡
        private int bankcardtype;
        //银行卡名称
        private String bankname;
        //卡号后4位
        private String cardlast;

        public int getBankcardtype() {
            return bankcardtype;
        }

        public void setBankcardtype(int bankcardtype) {
            this.bankcardtype = bankcardtype;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getCardlast() {
            return cardlast;
        }

        public void setCardlast(String cardlast) {
            this.cardlast = cardlast;
        }

        public String getYborderid() {
            return yborderid;
        }

        public void setYborderid(String yborderid) {
            this.yborderid = yborderid;
        }

        public String getMerchantaccount() {
            return merchantaccount;
        }

        public void setMerchantaccount(String merchantaccount) {
            this.merchantaccount = merchantaccount;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }
    }

    public static class PayResponse extends Response {

        protected String orderid;
        protected String phone;
        protected Integer smsconfirm;
        protected Integer sendtime;

        public boolean needConfirm() {
            return smsconfirm == 1;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getSmsconfirm() {
            return smsconfirm;
        }

        public void setSmsconfirm(Integer smsconfirm) {
            this.smsconfirm = smsconfirm;
        }

        public Integer getSendtime() {
            return sendtime;
        }

        public void setSendtime(Integer sendtime) {
            this.sendtime = sendtime;
        }
    }

    public static class Pay {

        private final static ResourceBundle rb = ResourceBundle.getBundle("payapi");
        private final static String MERCHANTACCOUNT = rb.getString("payapi.merchantaccount");
        private final static String CALLBACKURL = rb.getString("payapi.callback");

        public final static int CURRENCY_RMB = 156;
        public final static String PRODUCT_CATALOG_VIRTUAL = "1";
        public final static int IDENTITY_TYPE_USERID = 2;

        //商户账户编号
        private String merchantaccount = MERCHANTACCOUNT;
        //客户订单号
        private String orderid;
        //交易时间
        private int transtime;
        //交易币种 默认156
        private int currency = CURRENCY_RMB;
        //交易金额  以"分"为单位的整型，必须大于零
        private int amount;
        //商品类别码
        private String productcatalog = PRODUCT_CATALOG_VIRTUAL;
        //商品名称
        private String productname;
        //商品描述
        private String productdesc;
        //用户标识
        private String identityid;
        //用户标识类型
        private int identitytype = IDENTITY_TYPE_USERID;
        // 终端标识类型  0 ：IMEI 1：MAC 2：UUID（针对IOS系统）3：OTHER
        private int terminaltype;
        //终端标识
        private String terminalid;
        //其他支付信息
        private String other;
        //用户IP
        private String userip;
        //商户后台系统的回调地址
        private String callbackurl = CALLBACKURL;

        public String getCallurl() {
            return "no url";
        }

        public String getMerchantaccount() {
            return merchantaccount;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public int getTranstime() {
            return transtime;
        }

        public void setTranstime(int transtime) {
            this.transtime = transtime;
        }

        public int getCurrency() {
            return currency;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getProductcatalog() {
            return productcatalog;
        }

        public String getProductname() {
            return productname;
        }

        public void setProductname(String productname) {
            this.productname = productname;
        }

        public String getProductdesc() {
            return productdesc;
        }

        public void setProductdesc(String productdesc) {
            this.productdesc = productdesc;
        }

        public String getIdentityid() {
            return identityid;
        }

        public void setIdentityid(String identityid) {
            this.identityid = identityid;
        }

        public int getIdentitytype() {
            return identitytype;
        }

        public int getTerminaltype() {
            return terminaltype;
        }

        public void setTerminaltype(int terminaltype) {
            this.terminaltype = terminaltype;
        }

        public String getTerminalid() {
            return terminalid;
        }

        public void setTerminalid(String terminalid) {
            this.terminalid = terminalid;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public String getUserip() {
            return userip;
        }

        public void setUserip(String userip) {
            this.userip = userip;
        }

        public String getCallbackurl() {
            return callbackurl;
        }

    }

}
