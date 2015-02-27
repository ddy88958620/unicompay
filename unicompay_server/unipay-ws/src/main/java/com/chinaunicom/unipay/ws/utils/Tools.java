package com.chinaunicom.unipay.ws.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 公用工具类
 * Created by zhushuai on 2014/11/17.
 */
public class Tools {
    /**
     * 是否为NULL或空
     *
     * @return true NULL  false 非NULL
     */
    public static boolean isNull(String str){
        if(null == str || str.trim().equals("")){
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取当前时间
     *
     * @return yyyyMMddHHmmss
     * */
    public static String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String mDateTime = formatter.format(c.getTime());
        String str = mDateTime.substring(0, 14);
        return str;
    }
    /**
     * 获取UUID通用唯一识别码
     * */
    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        String str =uuid.toString();
        String rtStr = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return rtStr;
    }
    /**
     *转换系统时间
     * */
    public static String getCommonTime(int time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date((long)time * 1000);
        String str = formatter.format(date);
        return  str;
    }

   }
