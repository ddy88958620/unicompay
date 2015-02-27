package com.unicom.sms.util;

/**
 * Created by zhaofrancis on 15/1/23.
 */
public class MobilePhoneUtil {
    public static String format(String mobilePhone) {
        if (null == mobilePhone || mobilePhone.length() < 11) {
            return null;
        }

        return mobilePhone.substring(mobilePhone.length() - 11);
    }
}
