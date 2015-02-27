package com.chinaunicom.unipay.ws.persistence;

import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2014/11/28
 * Time: 15:03
 */
public class OrderTest {

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

        rp.addMapping(Order.TABLE, "orderid", Order.class);
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
}
