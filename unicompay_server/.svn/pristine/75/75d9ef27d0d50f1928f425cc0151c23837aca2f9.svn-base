package com.unicom.sms.service.http;

import com.unicom.sms.bean.SMSBean;
import com.unicom.sms.exception.ServiceException;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhaofrancis on 15/1/22.
 */
public class HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);
    private static CloseableHttpClient client = null;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(350);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(5000).build();

        client = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
    }

    public static void sendRequest(SMSBean smsBean) throws ServiceException {

        HttpPost post = new HttpPost(smsBean.getHandlerURL());

        try {

//            ClientHttpResponse response = httpRequestFactory.createRequest(new URI(""), HttpMethod.POST).execute();
            CloseableHttpResponse response = client.execute(post);
            int responseCode = response.getStatusLine().getStatusCode();
            String responseStr = EntityUtils.toString(response.getEntity());
            response.close();

            if (HttpStatus.SC_OK != responseCode) {
//                throw new ServiceException("send request to " + smsBean.getHandlerURL() + ", error response: " + responseStr);
                throw new ServiceException(responseStr);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
//            LOGGER.error("send request error " + smsBean.getHandlerURL() + ", " + e.getMessage(), e);
            throw new ServiceException(e ,e.getMessage());
        }
    }
}
