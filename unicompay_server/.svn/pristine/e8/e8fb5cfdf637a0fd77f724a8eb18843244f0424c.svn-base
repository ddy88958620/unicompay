package com.chinaunicom.unipay.ws.plugins.ioc;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * User: Frank
 * Date: 2014/3/26
 * Time: 11:17
 */
public class IocInterceptor implements Interceptor {

    static ApplicationContext ctx;

    public void intercept(ActionInvocation ai) {
        Controller controller = ai.getController();
        Field[] fields = controller.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object bean = null;

            if(field.isAnnotationPresent(Resource.class)) {
                Resource rs = field.getAnnotation(Resource.class);
                if(rs.name().isEmpty()) {
                    bean = field.getGenericType().toString().equals("interface org.springframework.context.ApplicationContext") ? ctx : ctx.getBean(field.getType());
                } else {
                    bean = ctx.getBean(rs.name());
                }
            } else {
                continue;
            }

            try {
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(controller, bean);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ai.invoke();
    }
}
