package com.chinaunicom.unipay.ws.services.impl;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.services.IHttpService;
import com.chinaunicom.unipay.ws.services.ISMSService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by jackj_000 on 2015/2/27 0027.
 */
public class SMSService implements ISMSService{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Prop prop = PropKit.use("payapi.properties","utf-8");
    private final String url = prop.get("smsurl");
    @Resource
    IHttpService ihs;
    @Override
    public SmsResponse charge(SmsRequest sr) throws Exception {
        String res = ihs.httpPost(url, JSON.toJSONString(sr),null,"UTF-8");
        return JSON.parseObject(res,SmsResponse.class);
    }
}
