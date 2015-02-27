package com.unicom.order.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisManager {
    private static final String PREFIX = "ORDER";

    @Autowired
	private RedisTemplate<Serializable, Serializable> cacheTemplate;
	
//	private RedisManager(String argsPrefix, String groupName) {
////		super(argsPrefix, groupName);
//	}

	public int delete(Serializable key) {
		if (key == null) {
			return 0;
		}
		cacheTemplate.delete(RedisManager.PREFIX + key);
		return 1;
	}

	public void destroy() {
	}

	public Serializable get(Serializable key) {
		if (key == null) {
			return null;
		}
		ValueOperations<Serializable, Serializable> valueOperations = cacheTemplate.opsForValue();
		
		return valueOperations.get(RedisManager.PREFIX + key);
	}

	public synchronized void init() {
	}

	public int invalid(Serializable key) {
		if (key == null) {
			return 0;
		}
		return cacheTemplate.hasKey(RedisManager.PREFIX + key) ? 1 : 0;
	}

	public List<Serializable> mget(List<? extends Object> keys) {
		return null;
	}

	public Serializable put(Serializable key,
			Serializable value, int version, int expireTime) {
		if (key == null) {
			return null;
		}
		ValueOperations<Serializable, Serializable> valueOperations = cacheTemplate.opsForValue();
		valueOperations.set(RedisManager.PREFIX + key, value, expireTime, TimeUnit.SECONDS);
		return value;
	}

	public Serializable put(Serializable key,
			Serializable value, int version) {
		if (key == null) {
			return null;
		}
		ValueOperations<Serializable, Serializable> valueOperations = cacheTemplate.opsForValue();
		valueOperations.set(RedisManager.PREFIX + key, value);
		return value;
	}

	public Serializable put(Serializable key, Serializable value) {
		if (key == null) {
			return null;
		}
		ValueOperations<Serializable, Serializable> valueOperations = cacheTemplate.opsForValue();
		valueOperations.set(RedisManager.PREFIX + key, value);
		return value;
	}

	public void setCacheTemplate(RedisTemplate<Serializable, Serializable> cacheTemplate) {
		this.cacheTemplate = cacheTemplate;
	}

}
