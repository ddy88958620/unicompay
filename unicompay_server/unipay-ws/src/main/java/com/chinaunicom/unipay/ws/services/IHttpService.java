package com.chinaunicom.unipay.ws.services;

import java.util.Map;

/**
 * Created by Administrator on 2015/1/22 0022.
 */
public interface IHttpService {

    public String httpPost(String url, String param, Map<String, String> map, String encode) throws Exception;

    public String httpGet(String url, String param, String encode) throws Exception;
}
