package com.chinaunicom.unipay.ws.services.impl;

import com.chinaunicom.unipay.ws.services.IMessageService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * User: Frank
 * Date: 2015/1/20
 * Time: 11:20
 */
@Service
public class MessageService implements IMessageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static Prop prop = PropKit.use("payapi.properties", "utf-8");
    private final static String msgurl = prop.get("paymsg.url");

    @Resource
    private CloseableHttpClient c;
    @Resource
    private ExecutorService e;

    @Override
    public boolean notify(final Message msg) throws Exception {

        e.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final String url = msgurl + "?" + "ordertime=" + msg.getPaytime() + "&recordindex=" + msg.getOrderid() + "&orderid=" + msg.getCporderid() + "&paytype=" + msg.getPaytype() + "&cpid=" + msg.getCpid() + "&serviceid=" + msg.getServiceid() + "&payfee=" + msg.getPayfee() + "&hret=" + msg.getPayresult() + "&status=" + String.valueOf(msg.getStatus());

                final HttpGet get = new HttpGet(url);
                final long start = System.currentTimeMillis();
                CloseableHttpResponse rsp = null;
                try {
                    rsp = c.execute(get);
                } finally {
                    if (rsp != null) {
                        rsp.close();
                    }
                }
                final boolean isSuccess = rsp.getStatusLine().getStatusCode() == 200;
                logger.debug("消息通知请求完成|URL=" + url + "|用时=" + (System.currentTimeMillis() - start) + "ms");
                return isSuccess;
            }
        });

        return true;
    }
}
