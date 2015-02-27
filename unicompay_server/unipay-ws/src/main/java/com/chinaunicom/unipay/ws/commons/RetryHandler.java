package com.chinaunicom.unipay.ws.commons;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * User: Frank
 * Date: 2014/12/30
 * Time: 11:58
 */
@Component
public class RetryHandler implements HttpRequestRetryHandler {

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

        if(executionCount > 3) {
            return false;
        }

        if(exception instanceof IOException) {
            return true;
        }

        return false;
    }
}
