package com.chinaunicom.unipay.ws.persistence;

import com.jfinal.plugin.activerecord.Model;

/**
 * User: Frank
 * Date: 2014/11/28
 * Time: 14:00
 */
public class UserInfo extends Model<UserInfo> {
    public static final UserInfo dao = new UserInfo();
    public static final String TABLE = "zxdbm_misp.muser_basicinfo";
    public static final String ID = "userindex";

    public UserInfo getByCpid(String cpid) {
        return findFirst("select mb.userindex,mb.userid,mb.prmspcode from " + TABLE + " mb where (mb.usertype = 6 or mb.usertype = 2) and mb.prmspcode = ?", cpid);
    }

    public int getUserindex() {
        return getBigDecimal("userindex").intValue();
    }

    public String getUserid() {
        return get("userid");
    }

    public String getPrmspcode() {
        return get("prmspcode");
    }

    public Signup getSignup() {
        return Signup.dao.findFirst("select * from " + Signup.TABLE + " where userindex = ?", getUserindex());
    }

    public static class Signup extends Model<Signup> {
        public static final Signup dao = new Signup();
        public static final String TABLE = "zxdbm_appstore.muser_signup_develop";
        public static final String ID = "signupindex";

        public String getSecretkey() {
            return get("secretkey");
        }
    }
}
