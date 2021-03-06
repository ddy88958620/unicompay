package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * User: Frank
 * Date: 2014/11/27
 * Time: 13:54
 */
public class Order extends Model<Order> {

    private static final Logger logger = LoggerFactory.getLogger(Order.class);

    public static final Order dao = new Order();
    public static final String TABLE = "unipay_payorder";
    public static final String ID = "orderid";

    public PayStatistic getPayStatistic(String cardno,String useraccount,String time){

        String daytime = time.substring(0,8);
        String monthtime = time.substring(0,6);

        Order dayorder = findFirst("select count(1) as daycount,sum(payfee) as dayfee from " + TABLE + " where status = 0 and cardno = ? and useraccount = ? and substr(paytime,0,8) = ? group by cardno,useraccount", cardno, useraccount, daytime);
        PayStatistic ps = new PayStatistic();

        Order monthorder = findFirst("select count(1) as monthcount,sum(payfee) as monthfee from " + TABLE + " where status = 0 and cardno = ? and useraccount = ? and substr(paytime,0,6) = ? group by cardno,useraccount", cardno, useraccount, monthtime);

        ps.setDayfee(dayorder.getBigDecimal("dayfee").intValue());
        ps.setDaycount(dayorder.getBigDecimal("daycount").intValue());
        ps.setMonthfee(monthorder.getBigDecimal("monthfee").intValue());
        ps.setMonthcount(monthorder.getBigDecimal("monthcount").intValue());

        return ps;
    }

    @Override
    public boolean update() {

        final long start = System.currentTimeMillis();
        final boolean result = super.update();

        logger.debug("订单更新[" + getOrderid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    @Override
    public boolean save() {

        final long start = System.currentTimeMillis();
        final boolean result = super.save();

        logger.debug("订单创建[" + getOrderid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    public Order() {
        setIswbpay(0);
        setSelectammount(0);
        setStatus(2);
        setPayresult("99999");
    }

    public String getOrderid() {
        return get("orderid");
    }

    public Order setOrderid(String orderid) {
        set("orderid", orderid);
        return this;
    }

    public Order setStatus(int status) {
        set("status", new BigDecimal(status));
        return this;
    }

    public int getStatus() {
        return getBigDecimal("status").intValue();
    }

    public Order setPayflowid(String payflowid) {
        set("payflowid", payflowid);
        return this;
    }

    public String getPayflowid() {
        return get("payflowid");
    }

    public Order setEncryptparam(String encryptparam) {
        set("encryptparam", encryptparam);
        return this;
    }

    public Order setOrderid_3rd(String cporderid) {
        set("orderid_3rd", cporderid);
        return this;
    }

    public String getOrderid_3rd() {
        return get("orderid_3rd");
    }

    public Order setServicekey(String servicekey) {
        set("servicekey", servicekey);
        return this;
    }

    public String getServicekey(){
        return get("servicekey");
    }

    public Order setOrdertime(String ordertime){
        set("ordertime",ordertime);
        return this;
    }

    public Order setImsi(String imsi) {
        set("imsi", imsi);
        return this;
    }

    public Order setUseraccount(String useraccount) {
        set("useraccount", useraccount);
        return this;
    }

    public String getUseraccount(){
        return get("useraccount");
    }

    public Order setPaytime(String paytime) {
        set("paytime", paytime);
        return this;
    }

    public String getPaytime() {
        return get("paytime");
    }

    public Order setSdkversion(String sdkversion) {
        set("sdkversion", sdkversion);
        return this;
    }

    public Order setChannelid(String channelid) {
        set("channelid", channelid);
        return this;
    }

    public String getChannelid() {
        return get("channelid");
    }

    public Order setEmpno(String empno) {
        set("empno", empno);
        return this;
    }

    public Order setCpid(String cpid) {
        set("cpid", cpid);
        return this;
    }

    public String getCpid() {
        return get("cpid");
    }

    public Order setPointid(String pointid) {
        set("pointid", pointid);
        return this;
    }

    public String getPointid() {
        return get("pointid");
    }

    public Order setUserindex(int userindex) {
        set("userindex", userindex);
        return this;
    }

    public Order setUserid(String userid) {
        set("userid", userid);
        return this;
    }

    public Order setProductindex(int productindex) {
        set("productindex", productindex);
        return this;
    }

    public Order setProductid(String productid) {
        set("productid", productid);
        return this;
    }

    public String getProductid() {
        return get("productid");
    }

    public Order setProductname(String productname) {
        set("productname", productname);
        return this;
    }

    public String getProductname() {
        return get("productname");
    }

    public Order setPointindex(int pointindex) {
        set("pointindex", pointindex);
        return this;
    }

    public Order setPointname(String pointname) {
        set("pointname", pointname);
        return this;
    }

    public String getPointname() {
        return get("pointname");
    }

    public Order setPayfee(int payfee) {
        set("payfee", new BigDecimal(payfee));
        return this;
    }

    public int getPayfee() {
        return getBigDecimal("payfee").intValue();
    }

    public Order setPayresult(String payresult) {
        set("payresult", payresult);
        return this;
    }

    public String getPayresult() {
        return get("payresult");
    }

    public String getErrorcode(){
        return get("errorcode");
    }

    public Order setErrorcode(String errorcode){
        set("errorcode",errorcode);
        return this;
    }

    public Order setCardno(String cardno){
        set("cardno",cardno);
        return this;
    }

    public String getCardno(){
        return get("cardno");
    }

    public Order setUsertype(String usertype){
        set("usertype",usertype);
        return this;
    }
    public String getUsertype(){
        return get("usertype");
    }

    public Order setSelectammount(int selectammount){
        set("selectammount",new BigDecimal(selectammount));
        return this;
    }
    public int getSelectammount(){
        return getBigDecimal("selectammount").intValue();
    }

    public Order setIswbpay(int iswbpay){
        set("iswbpay", new BigDecimal(iswbpay));
        return this;
    }
    public int getIswbpay(){
        return getBigDecimal("iswbpay").intValue();
    }

    public static class PayStatistic {
        private int daycount;
        private int dayfee;
        private int monthcount;
        private int monthfee;

        public int getMonthcount() {
            return monthcount;
        }

        public void setMonthcount(int monthcount) {
            this.monthcount = monthcount;
        }

        public int getMonthfee() {
            return monthfee;
        }

        public void setMonthfee(int monthfee) {
            this.monthfee = monthfee;
        }

        public int getDaycount() {
            return daycount;
        }

        public void setDaycount(int daycount) {
            this.daycount = daycount;
        }

        public int getDayfee() {
            return dayfee;
        }

        public void setDayfee(int dayfee) {
            this.dayfee = dayfee;
        }
    }
    static enum Encryptparam{
        //支付宝支付类型
        ALIPAY("08"),

        //易宝支付类型
        YIBAOPAY("11"),

        //19PAY支付类型
        CARD19PAY("13"),

        //沃币支付类型
        WOCOIN("14"),

        //支付宝扫码支付类型
        ALIPAYQRCODE("15");


        private String param;

        Encryptparam(String param) {
            this.param = param;
        }

        public String getParam() {
            return param;
        }
    }
}
