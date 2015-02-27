package com.chinaunicom.unipay.ws.services;

import com.chinaunicom.unipay.ws.commons.ErrorCode;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * User: Frank
 * Date: 2015/1/12
 * Time: 15:38
 */
public interface IChargeService {
    /**
     * 19PAY充值卡支付申请
     */
    public ChargeResponse charge(Charge charge) throws Exception;

    public static class ChargeResponse {
        private String versionid;
        private String merchantid;
        private String verifystring;
        private String orderdate;
        private String orderid;
        private String amount;
        private String currency;
        private String paysq;
        private String paydate;
        private String pcid;
        private String pmid;
        private String result;
        private String resultstr;

        @JacksonXmlProperty(localName = "version_id")
        public String getVersionid() {
            return versionid;
        }

        public void setVersionid(String versionid) {
            this.versionid = versionid;
        }
        @JacksonXmlProperty(localName = "merchant_id")
        public String getMerchantid() {
            return merchantid;
        }

        public void setMerchantid(String merchantid) {
            this.merchantid = merchantid;
        }

        public String getVerifystring() {
            return verifystring;
        }

        public void setVerifystring(String verifystring) {
            this.verifystring = verifystring;
        }
        @JacksonXmlProperty(localName = "order_date")
        public String getOrderdate() {
            return orderdate;
        }

        public void setOrderdate(String orderdate) {
            this.orderdate = orderdate;
        }
        @JacksonXmlProperty(localName = "order_id")
        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
        @JacksonXmlProperty(localName = "pay_sq")
        public String getPaysq() {
            return paysq;
        }

        public void setPaysq(String paysq) {
            this.paysq = paysq;
        }
        @JacksonXmlProperty(localName = "pay_date")
        public String getPaydate() {
            return paydate;
        }

        public void setPaydate(String paydate) {
            this.paydate = paydate;
        }
        @JacksonXmlProperty(localName = "pc_id")
        public String getPcid() {
            return pcid;
        }

        public void setPcid(String pcid) {
            this.pcid = pcid;
        }
        @JacksonXmlProperty(localName = "pm_id")
        public String getPmid() {
            return pmid;
        }

        public void setPmid(String pmid) {
            this.pmid = pmid;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResultstr() {
            return resultstr;
        }

        public void setResultstr(String resultstr) {
            this.resultstr = resultstr;
        }

        public boolean isSucess(){return "P".equals(result);}

        public int toUnipayCode() {
            if(isSucess()) {
                return 0;
            } else {
                return (Integer.parseInt(ErrorCode.valueOf(ErrorCode.PAY19_CODE_ + resultstr).getValue()));
            }
        }

    }

    public static class Charge {
        private String versionid;
        private String merchantid;
        private String verifystring;
        private String orderdate;
        private String orderid;
        private String amount;
        private String cardnum1;
        private String cardnum2;
        private String currency;
        private String pmid;
        private String pcid;
        private String returl;
        private String notifyurl;
        private String retmode;
        private String selectamount;
        private String orderpdesc;
        private String username;
        private String userphone;
        private String usermobile;
        private String useremail;

        public String getVersionid() {
            return versionid;
        }

        public void setVersionid(String versionid) {
            this.versionid = versionid;
        }

        public String getMerchantid() {
            return merchantid;
        }

        public void setMerchantid(String merchantid) {
            this.merchantid = merchantid;
        }

        public String getVerifystring() {
            return verifystring;
        }

        public void setVerifystring(String verifystring) {
            this.verifystring = verifystring;
        }

        public String getOrderdate() {
            return orderdate;
        }

        public void setOrderdate(String orderdate) {
            this.orderdate = orderdate;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

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

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPmid() {
            return pmid;
        }

        public void setPmid(String pmid) {
            this.pmid = pmid;
        }

        public String getPcid() {
            return pcid;
        }

        public void setPcid(String pcid) {
            this.pcid = pcid;
        }

        public String getReturl() {
            return returl;
        }

        public void setReturl(String returl) {
            this.returl = returl;
        }

        public String getNotifyurl() {
            return notifyurl;
        }

        public void setNotifyurl(String notifyurl) {
            this.notifyurl = notifyurl;
        }

        public String getRetmode() {
            return retmode;
        }

        public void setRetmode(String retmode) {
            this.retmode = retmode;
        }

        public String getSelectamount() {
            return selectamount;
        }

        public void setSelectamount(String selectamount) {
            this.selectamount = selectamount;
        }

        public String getOrderpdesc() {
            return orderpdesc;
        }

        public void setOrderpdesc(String orderpdesc) {
            this.orderpdesc = orderpdesc;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserphone() {
            return userphone;
        }

        public void setUserphone(String userphone) {
            this.userphone = userphone;
        }

        public String getUsermobile() {
            return usermobile;
        }

        public void setUsermobile(String usermobile) {
            this.usermobile = usermobile;
        }

        public String getUseremail() {
            return useremail;
        }

        public void setUseremail(String useremail) {
            this.useremail = useremail;
        }
    }
}
