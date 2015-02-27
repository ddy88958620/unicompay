package com.unicom.tv.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {

	String getNewSignKey(String imei);
	
	Object get(String key);
	
	void set(String key, Object value);

	void set(String key, Object value, long time, TimeUnit timeUnit);
	
	void delete(String key);


}
