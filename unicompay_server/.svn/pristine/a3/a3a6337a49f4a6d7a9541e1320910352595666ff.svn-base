package com.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.unicom.tv.common.constants.CacheConstants;
import com.unicom.tv.service.CacheService;
import com.unicom.tv.service.impl.CacheServiceImpl;

public class TestCache {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/spring/spring-mvc.xml", "/spring/spring-context.xml");
		CacheService cacheService = context.getBean(CacheServiceImpl.class);
	}

}
