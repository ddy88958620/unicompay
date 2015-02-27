package com.chinaunicom.unipay.ws.controllers;

import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.utils.CryptUtil;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.encrypt.RSA;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by lynne on 2015/1/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/chinaunicom/unipay/ws/controllers/ctx.xml")
public class AliPayControllerTest{


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String aeskey = RandomUtil.getRandom(16);

    @Resource CloseableHttpClient httpClient;
    @Resource Map<String, String> config;
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
        cp.start();
        rp.start();
    }

    public String request(String api, String data) throws Exception {

        data = CryptUtil.encryptBy3DesAndBase64(data, aeskey);
        String encryptkey = RSA.encrypt(aeskey, config.get("serverPublicKey"));

        HttpPost request = new HttpPost("http://210.22.123.94:18081/unicompay_wl/alipay/" + api + "?key=" + URLEncoder.encode(encryptkey, "UTF-8"));
        request.setEntity(new StringEntity(data));
        CloseableHttpResponse response = httpClient.execute(request);

        data = CryptUtil.decryptBy3DesAndBase64(IOUtils.toString(response.getEntity().getContent(), "UTF-8"), aeskey);
        data = URLDecoder.decode(data, "UTF-8");
        return data;
    }

    static final String ALIPAY = "{\n" +
            "    \"optype\": 0,\n" +
            "    \"cporderid\": \"_cporderid\",\n" +
            "    \"feetype\": 1,\n" +
            "    \"payfee\": 10,\n" +
            "    \"ordertime\": \"_ordertime\",\n" +
            "    \"serviceid\": \"SDKAliPay\",\n" +
            "    \"consumecode\": \"9000286920141028153804390200001\",\n" +
            "    \"cpid\": \"86000136\",\n" +
            "    \"channelid\": \"00012243\",\n" +
            "    \"identityid\": \"447769804451069\",\n" +
            "    \"assistantid\": \"1\",\n" +
            "    \"sdkversion\": \"2.0.0\",\n" +
            "}";
    @Test
    public void testPay() throws Exception{


        String ordertime = Tools.getCurrentTime();
        String uuid = Tools.getUUID();

        String pay = ALIPAY.replace("_cporderid", uuid).replace("_ordertime", ordertime);
        String data = request("pay",pay);

        logger.info(data);

    }

    @Test
    public void testCallback()throws Exception{

    }

}
