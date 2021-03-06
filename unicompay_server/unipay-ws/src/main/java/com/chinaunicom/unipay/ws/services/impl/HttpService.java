package com.chinaunicom.unipay.ws.services.impl;

import com.chinaunicom.unipay.ws.services.IHttpService;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/22 0022.
 */
@Service
public class HttpService implements IHttpService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CloseableHttpClient httpClient;
    @Override
    public String httpPost(String url, String param, Map<String, String> map, String encode) throws Exception {
        String res = null;
        HttpPost request = new HttpPost(url);
        if(map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        request.setEntity(new StringEntity(param, encode));
        logger.debug("URL:"+url+"param:"+param);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                res = IOUtils.toString(response.getEntity().getContent(), encode);
            } else {
                res = response.getStatusLine().getStatusCode() + "";
            }
            logger.debug("请求返回状态码：" + response.getStatusLine().getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            response.close();
        }
        return res;
    }

    @Override
    public String httpGet(String url, String param, String encode) throws Exception {
        String res = null;
        HttpGet request = new HttpGet(url+"?"+param);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                res = IOUtils.toString(response.getEntity().getContent(), encode);
            } else {
                res = response.getStatusLine().getStatusCode() + "";
            }
            logger.debug("请求返回状态码：" + response.getStatusLine().getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            response.close();
        }
        return res;
    }
}
