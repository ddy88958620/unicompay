package com.chinaunicom.unipay.ws.controllers;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

/**
 * User: Frank
 * Date: 2015/1/30
 * Time: 10:31
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public void intercept(ActionInvocation ai) {

        Controller controller = ai.getController();
        controller.getResponse().setHeader("Access-Control-Allow-Origin", "*");

        ai.invoke();

    }
}
