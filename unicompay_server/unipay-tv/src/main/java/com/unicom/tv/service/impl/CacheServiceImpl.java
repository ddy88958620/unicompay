package com.unicom.tv.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.unicom.tv.common.constants.CacheConstants;
import com.unicom.tv.common.constants.RuntimeProperties;
import com.unicom.tv.common.constants.RuntimeProperties.RuntimeKey;
import com.unicom.tv.service.CacheService;

@Service
public class CacheServiceImpl implements CacheService {
	
	@Resource(name = "redisTemplate")
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public String getNewSignKey(String imei) {
		String key = UUID.randomUUID().toString();
		Long keyExpired = Long.valueOf(RuntimeProperties.get(RuntimeKey.SYSTEM_PAY_KEY_EXPIRES));
		this.delete(CacheConstants.PRIVATE_KEY + imei);
		this.set(CacheConstants.PRIVATE_KEY + imei, key, keyExpired, TimeUnit.DAYS);
		return key;
	}
	
	@Override
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void set(String key, Object value, long time, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, time, timeUnit);
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}

}
