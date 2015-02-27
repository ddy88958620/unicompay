package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * Created by zhushuai on 2014/12/19.
 */
public class Quota extends Model<Quota> {
    public static final Quota dao = new Quota();
    public static final String TABLE = "unipay_quota";
    public static final String ID = "useraccount";

    public List<Quota> getQuotaByAccount(String useraccount){
        List<Quota> quotas = Quota.dao.find("select * from " + TABLE + " where useraccount = ?",useraccount);
        return quotas;
    }

    public Quota getQuotaByCardno(String useraccount,String cardno){
        return findFirst("select * from " + TABLE + " where useraccount = ? and cardno = ?",useraccount,cardno);
    }

    public String getUseraccount(){
        return get("useraccount");
    }

    public Quota setUseraccount(String useraccount){
        set("useraccount",useraccount);
        return this;
    }

    public String getCardno(){
        return get("cardno");
    }

    public Quota setCardno(String cardno){
        set("cardno",cardno);
        return this;
    }

    public int getOncefee(){
        return getBigDecimal("oncefee").intValue();
    }

    public Quota setOncefee(int oncefee){
        set("oncefee",oncefee);
        return this;
    }

    public int getDayfee(){
        return getBigDecimal("dayfee").intValue();
    }

    public Quota setDayfee(int dayfee){
        set("dayfee",dayfee);
        return this;
    }

    public int getMonthfee(){
        return getBigDecimal("monthfee").intValue();
    }

    public Quota setMonthfee(int monthfee){
        set("monthfee",monthfee);
        return this;
    }

    public Quota setChangetime(String changetime){
        set("changetime",changetime);
        return this;
    }
}
