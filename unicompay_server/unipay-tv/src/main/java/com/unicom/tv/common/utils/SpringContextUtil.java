package com.unicom.tv.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		applicationContext = context;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name, Class<T> classes) {
		try {
			return (T) applicationContext.getBean(name);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static <T> T getBean(Class<T> classes) {
		return applicationContext.getBean(classes);
	}

}
