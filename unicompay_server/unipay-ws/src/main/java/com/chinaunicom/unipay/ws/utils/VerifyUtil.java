package com.chinaunicom.unipay.ws.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by jackj_000 on 2015/2/11 0011.
 */
public class VerifyUtil {
    private static final Logger logger = LoggerFactory.getLogger(VerifyUtil.class);
    public static String getVerify(Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()){
           sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }


    public static void logprint(String msg,Object obj){
        logger.info(msg+ JSON.toJSONString(obj));
    }

}
