package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhushuai on 2014/12/1.
 */
public class Notify extends Model<Notify> {

    private static final Logger logger = LoggerFactory.getLogger(Notify.class);

    public static final Notify dao = new Notify();
    public static final String TABLE = "unipay_notification";
    public static final String ID = "orderid";

    @Override
    public boolean save() {
        final long start = System.currentTimeMillis();
        final boolean result = super.save();

        logger.debug("通知创建[" + getOrderid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    @Override
    public boolean update() {

        final long start = System.currentTimeMillis();
        final boolean result = super.update();

        logger.debug("通知更新[" + getOrderid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    public List<Notify> getNotifyByWork(){
        List<Notify> notifys = Notify.dao.find("select * from " + TABLE + " where workstatus = 1");
        return notifys;
    }

    public Notify setNotifyindex(int notifyindex) {
        set("notifyindex",notifyindex);
        return this;
    }


    public Notify setOrderid_3rd(String orderid_3rd) {
        set("orderid_3rd",orderid_3rd);
        return this;
    }


    public Notify setOrderid(String orderid) {
        set("orderid",orderid);
        return this;
    }

    public String getOrderid(){
        return get("orderid");
    }


    public Notify setOrdertime(String ordertime) {
        set("ordertime",ordertime);
        return this;
    }


    public Notify setLastnotifytime(String lastnotifytime) {
        set("lastnotifytime",lastnotifytime);
        return this;
    }


    public Notify setCpid(String cpid) {
        set("cpid",cpid);
        return this;
    }


    public Notify setAppid(String appid) {
        set("appid",appid);
        return this;
    }


    public Notify setFid(String fid) {
        set("fid",fid);
        return this;
    }


    public Notify setConsumecode(String consumecode) {
        set("consumecode",consumecode);
        return this;
    }


    public Notify setHret(String hret) {
        set("hret",hret);
        return this;
    }


    public Notify setCpkey(String cpkey) {
        set("cpkey",cpkey);
        return this;
    }


    public Notify setSignmsg(String signmsg) {
        set("signmsg",signmsg);
        return this;
    }


    public Notify setRtnurl(String rtnurl) {
        set("rtnurl",rtnurl);
        return this;
    }


    public Notify setCprtnval(int cprtnval) {
        set("cprtnval",cprtnval);
        return this;
    }


    public Notify setPayfee(int payfee) {
        set("payfee",payfee);
        return this;
    }


    public Notify setPaytype(int paytype) {
        set("paytype",paytype);
        return this;
    }


    public Notify setStatus(int status) {
        set("status",status);
        return this;
    }


    public Notify setIscpreturned(int iscpreturned) {
        set("iscpreturned",iscpreturned);
        return this;
    }


    public Notify setMaxsendtimes(int maxsendtimes) {
        set("maxsendtimes",maxsendtimes);
        return this;
    }


    public Notify setSendtimes(int sendtimes) {
        set("sendtimes",sendtimes);
        return this;
    }

    public int getSendtimes(){
        return getBigDecimal("sendtimes").intValue();
    }

    public Notify setWorkstatus(int workstatus) {
        set("workstatus",workstatus);
        return this;
    }

    public Notify setCorrelator(String correlator) {
        set("correlator",correlator);
        return this;
    }

    public Notify setCptype(int cptype) {
        set("cptype",cptype);
        return this;
    }

    public String getAppid() {
        return getStr("appid");
    }

    public String getConsumecode() {
        return getStr("consumecode");
    }

    public String getCpid() {
        return getStr("cpid");
    }

    public String getFid() {
        return getStr("fid");
    }

    public String getOrderid_3rd() {
        return getStr("orderid_3rd");
    }

    public String getOrdertime() {
        return getStr("ordertime");
    }

    public int getPayfee() {
        return getBigDecimal("payfee").intValue();
    }

    public String getHret() {
        return getStr("hret");
    }
}
