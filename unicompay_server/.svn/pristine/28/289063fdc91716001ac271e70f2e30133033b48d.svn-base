package com.chinaunicom.unipay.ws.plugins.ioc;

import com.jfinal.plugin.IPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * User: Frank
 * Date: 2014/10/29
 * Time: 11:16
 */
public class IocPlugin implements IPlugin {

    private String[] configurations;
    private ApplicationContext ctx;

    /**
     * Use configuration under the path of WebRoot/WEB-INF.
     */
    public IocPlugin(String... configurations) {
        this.configurations = configurations;
    }

    public IocPlugin(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public boolean start() {
        if (ctx != null)
            IocInterceptor.ctx = ctx;
        else if (configurations != null)
            IocInterceptor.ctx = new FileSystemXmlApplicationContext(configurations);
        else
            IocInterceptor.ctx = new ClassPathXmlApplicationContext(configurations);
        return true;
    }

    public boolean stop() {
        return true;
    }

}
