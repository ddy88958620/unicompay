package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by zhushuai on 2015/1/5.
 */
public class CheckPoint extends Model<CheckPoint> {
    public static final CheckPoint dao  = new CheckPoint();
    public static final String TABLE = "unipay_log_checkpoint";
    public static final String ID = "uuid";

    public CheckPoint getCheckPoint(String consumecode,String userid){
        return findFirst("select * from " + TABLE + " where consume_code = ? and u.user_id = ?",consumecode,userid);
    }

    public CheckPoint setUuid(String uuid){
        set("uuid",uuid);
        return this;
    }

    public String getUuid(){
        return get("uuid");
    }

    public CheckPoint setAppid(String appid){
        set("app_id",appid);
        return this;
    }

    public String getAppid(){
        return get("app_id");
    }

    public CheckPoint setConsumecode(String consumecode){
        set("consume_code",consumecode);
        return this;
    }

    public String getConsumecode(){
        return get("consume_code");
    }

    public CheckPoint setUserid(String userid){
        set("user_id",userid);
        return this;
    }
    public String getUserid(){
        return get("user_id");
    }

    public CheckPoint setCreatetime(String createtime){
        set("create_time",createtime);
        return this;
    }

    public String getCreatetime(){
        return get("create_time");
    }
}
