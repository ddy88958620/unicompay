package com.chinaunicom.unipay.ws.persistence;

import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.ResourceBundle;
import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2014/11/28
 * Time: 15:03
 */
public class NotifyTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeClass
    public static void init() {

        ResourceBundle resdb = ResourceBundle.getBundle("db");

        C3p0Plugin cp = new C3p0Plugin(resdb.getString("oracle.url"),
                resdb.getString("oracle.username"),
                resdb.getString("oracle.password"),
                resdb.getString("oracle.driver"));
        ActiveRecordPlugin rp = new ActiveRecordPlugin(cp);
        rp.setShowSql(true);
        rp.setDialect(new OracleDialect());
        rp.setContainerFactory(new CaseInsensitiveContainerFactory());

        rp.addMapping(Order.TABLE, Order.ID, Order.class);
        rp.addMapping(ChargePoint.TABLE,ChargePoint.ID,ChargePoint.class);
        rp.addMapping(UserInfo.TABLE,UserInfo.ID,UserInfo.class);

        cp.start();
        rp.start();
    }

    @Test
    public void test() throws Exception {

        Order order = Order.dao.findById("d68f73e40020496eb8cfd3248b3f7bc3");
        assertTrue(order.getOrderid().equals("d68f73e40020496eb8cfd3248b3f7bc3"));

        String result = "003" + RandomUtil.getRandom(2);
        int status = (int) (new Random().nextDouble() * 100 / 10);

        order.setStatus(status).setPayresult(result).update();
        order = Order.dao.findById("d68f73e40020496eb8cfd3248b3f7bc3");
        assertTrue(order.getPayresult().equals(result));
        assertTrue(order.getStatus() == status);
    }


    @Test
    public void sendNotify() throws Exception{

        final String signMsg = MD5.MD5Encode(
                "orderid=" + "000000000000000000015037"
                + "&ordertime=" + "20150206145418"
                + "&cpid=" + "86007062"
                + "&appid=" + "907001101420141020154624339100"
                + "&fid=" + "00012243"
                + "&consumeCode=" + "907001101420141020154624339100017"
                + "&payfee=" + 12800
                + "&payType=" + 5
                + "&hRet=" + 0
                + "&status=" + 0
                + "&Key=" + "158f3069a435b314a80b");
        System.out.println(signMsg);
    }

}
