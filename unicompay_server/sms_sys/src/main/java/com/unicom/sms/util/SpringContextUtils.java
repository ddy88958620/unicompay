package com.unicom.sms.util;

import com.unicom.sms.exception.ServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by zhaofrancis on 15/1/19.
 */
public class SpringContextUtils implements ApplicationContextAware {
    private static SpringContextUtils instance;

    public static SpringContextUtils getInstance() {
        if (null == SpringContextUtils.instance) {
            throw new NullPointerException("SpringContextUtils instance is null");
        }
        return  SpringContextUtils.instance;
    }

    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.instance = this;
        this.applicationContext = applicationContext;
    }
}
