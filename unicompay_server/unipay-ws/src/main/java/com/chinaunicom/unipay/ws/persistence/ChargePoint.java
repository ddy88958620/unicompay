package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;

/**
 * User: Frank
 * Date: 2014/11/27
 * Time: 17:02
 */
public class ChargePoint extends Model<ChargePoint> {

    public static final ChargePoint dao = new ChargePoint();
    public static final String TABLE = "zxdbm_appstore.appc_charging_point";
    public static final String ID = "pointindex";

    public ChargePoint getByConsumecode(String consumecode) {
        return findFirst("select * from " + TABLE + " where pointid = ?", consumecode);
    }

    public ChargePoint getByServiceid(String serviceid){
        return findFirst("select * from " + TABLE + " where serviceid = ?",serviceid);
    }

    public Product getProduct() {
        return Product.dao.findById(getCntindex());
    }

    public int getPointindex() {
        return getBigDecimal("pointindex").intValue();
    }

    public String getPointid() {
        return get("pointid");
    }

    public String getPointname() {
        return get("pointname");
    }

    public String getPointtype() {
        return get("pointtype");
    }

    public int getChargingcodeindex() {
        return getBigDecimal("chargingcodeindex").intValue();
    }

    public int getPointvalue() {
        return getBigDecimal("pointvalue").intValue();
    }

    public String getServiceid() {
        return get("serviceid");
    }

    public int getCntindex() {
        return getBigDecimal("cntindex").intValue();
    }

    public static class Product extends Model<Product> {

        public static final Product dao = new Product();
        public static final String TABLE = "zxdbm_misp.mcnt_content";
        public static final String ID = "cntindex";

        public String getCntid() {
            return get("cntid");
        }

        public String getCntname() {
            return get("cntname");
        }

        public String getServicekey() {
            return get("servicekey");
        }

        public String getOnlineurl() {
            return get("onlineurl");
        }
    }
}
