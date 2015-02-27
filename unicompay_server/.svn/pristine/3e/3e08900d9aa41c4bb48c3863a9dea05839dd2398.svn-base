package com.unicom.order.interceptor;

import com.tydic.common.exception.InterfaceServiceException;
import com.tydic.common.interfaces.interceptors.plugins.AbstractPlugin;
import com.tydic.common.interfaces.interceptors.plugins.InterceptorPlugin;
import com.unicom.order.util.OrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sh-zhaogx3 on 2014/8/6.
 */
public class OrderInnerInterceptorPlugin extends AbstractPlugin implements InterceptorPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInnerInterceptorPlugin.class);

    private String innerServerIPs;

    private List<String> innerIPs = new ArrayList<String>();

    @Override
    public boolean before(HttpServletRequest request) throws InterfaceServiceException {
        String requestIP = OrderUtil.getRequestIP(request);
//        System.out.println(requestIP);
        if (null == innerIPs || innerIPs.size() == 0) {
            innerIPs = Arrays.asList(innerServerIPs.split(","));
        }
        if (innerIPs.contains(requestIP)) {
            return true;
        }
        LOGGER.error(requestIP + " is not authorized.");
        throw new InterfaceServiceException("1001", requestIP + " is not authorized");
    }

    @Override
    public boolean after(HttpServletRequest request) throws InterfaceServiceException {
        return true;
    }

    public String getInnerServerIPs() {
        return innerServerIPs;
    }

    public void setInnerServerIPs(String innerServerIPs) {
        this.innerServerIPs = innerServerIPs;
    }
}
