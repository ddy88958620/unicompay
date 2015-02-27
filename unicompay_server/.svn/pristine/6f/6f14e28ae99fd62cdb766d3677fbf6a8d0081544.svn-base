package com.unicom.vac.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by zhaofrancis on 15/2/11.
 */
public class VACUtil {
    private static Random random = new Random();
    private static SimpleDateFormat MDHMSDateFormat = new SimpleDateFormat("MMddHHmmss");
    private static SimpleDateFormat YMDHMSDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Get the value of 'SequenceId' in header, the length is 4.
     * @return
     */
    public static String getHexSequenceId() {
        return VACUtil.formatParam(VACUtil.random.nextInt(), 4);
    }

    public static String formatParam(int param, int length) {
        return StringUtils.leftPad(Integer.toHexString(param), length * 2, "0");
    }

    /**
     *
     * @param param
     * @param length if length <= param.length(), ignore length.
     * @return
     */
    public static String formatParam(String param, int length) {
        byte[] byteArray = param.getBytes();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            stringBuilder.append(Integer.toHexString(byteArray[i]));
        }

        if (param.length() >= length) {
            return stringBuilder.toString();
        }
        return StringUtils.rightPad(stringBuilder.toString(), length * 2, "0");
    }

    public static String getHexMDHMSDate() {
        return VACUtil.formatParam(VACUtil.MDHMSDateFormat.format(new Date()), 10);
    }

    public static String getHexYMDHMSDate() {
        return VACUtil.formatParam(VACUtil.YMDHMSDateFormat.format(new Date()), 14);
    }

    /**
     *
     * @param args without Chinese.
     * @return
     */
    public static String MD5(String ...args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : args) {
            stringBuilder.append(str);
        }
        return String.valueOf(DigestUtils.md5Digest(stringBuilder.toString().getBytes()));
    }

    public static String getRequestIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //多次反向代理,取第一个IP地址
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
