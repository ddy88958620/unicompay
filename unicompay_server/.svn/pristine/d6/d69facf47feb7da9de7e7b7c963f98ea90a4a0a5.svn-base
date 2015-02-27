package com.chinaunicom.unipay.ws.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;

import static com.chinaunicom.unipay.ws.utils.RedisUtil.Lock.DEFAULT_TIME_OUT;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.Lock.EXPIRE;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.Lock.LOCKED;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.Lock.ONE_MILLI_NANOS;
import static com.chinaunicom.unipay.ws.utils.RedisUtil.Lock.r;

/**
 * User: Frank
 * Date: 2014/12/25
 * Time: 11:48
 */
@Component
public class RedisUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final int SECOND = 1;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;

    @Resource(name = "jedisPool") Pool<Jedis> jp;

    public String hmset(final String key, final Map<String, String> value) {
        return hmset(key, value, 0);
    }

    public String hmset(final String key, final Map<String, String> value, final int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();
            String status = j.hmset(key, value);
            if (expire > 0)
                j.expire(key, expire);
            return status;
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public Map<String, String> hgetAll(final String key) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.hgetAll(key);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }

    }

    public long hset(final String key, final String field, final String value) {
        return hset(key, field, value, 0);
    }

    public long hset(final String key, final String field, final String value, final int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();
            long status = j.hset(key, field, value);
            if(expire > 0)
                j.expire(key, expire);
            return status;
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return 0;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public String hget(final String key, final String item) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.hget(key, item);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public long del(final String key) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.del(key);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return 0;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }

    }

    public String set(final String key, final String value) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.set(key, value);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public String setex(final String key, final String value, final int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.setex(key, expire, value);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public String get(final String key) {

        Jedis j = null;
        try {
            j = jp.getResource();
            return j.get(key);
        } catch(Exception e) {
            logger.error("缓存失效", e);
            return null;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public String getSet(String key, String value) {
        return getSet(key, value, 0);
    }

    public String getSet(String key, String value, int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();
            String result = j.getSet(key, value);
            if(expire > 0)
                j.expire(key, expire);
            return result;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public long setnx(final String key, final String value) {
        return setnx(key, value, 0);
    }

    public long setnx(final String key, final String value, final int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();
            long status = j.setnx(key, value);
            if(expire > 0)
                j.expire(key, expire);
            return status;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public boolean lock(final String key) {
        return lock(key, DEFAULT_TIME_OUT, EXPIRE);
    }

    public boolean lock(final String key, final long timeout) {
        return lock(key, timeout, EXPIRE);
    }

    public boolean lock(final String key, final int expire) {
        return lock(key, DEFAULT_TIME_OUT, expire);
    }

    public boolean lock(final String key, long timeout, final int expire) {

        Jedis j = null;
        try {
            j = jp.getResource();

            final long nano = System.nanoTime();
            timeout *= ONE_MILLI_NANOS;
            while ((System.nanoTime() - nano) < timeout) {
                if (j.setnx(key, LOCKED) == 1) {
                    j.expire(key, expire);
                    return true;
                }
                // 短暂休眠，nano避免出现活锁
                Thread.sleep(3, r.nextInt(500));
            }
        } catch (Exception e) {
            logger.error("缓存异常", e);
            //由于缓存异常，锁机制失效，放开所有锁判断
            return true;
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
        return false;
    }

    public void unlock(String key) {

        Jedis j = null;
        try {
            j = jp.getResource();
            j.del(key);
        } catch (Exception e) {
            logger.error("缓存异常", e);
        } finally {
            if(j != null)
                jp.returnResource(j);
        }
    }

    public boolean exists(String key){
        boolean flag = false;
        if(jp != null) {
            Jedis j = null;
            try {
                j = jp.getResource();
                flag =  j.exists(key);
                return flag;
            } catch (JedisConnectionException e) {
                logger.error("判断[" + key + "]是否存在失败，redis服务异常", e);
            } finally {
                if(j != null)
                    jp.returnResource(j);
            }

        } else {
            logger.error("redis服务无法访问");
        }
        return flag;

    }

    static class Lock {
        //加锁标志
        static final String LOCKED = "TRUE";
        static final long ONE_MILLI_NANOS = 1000000L;
        //默认超时时间（毫秒）
        static final long DEFAULT_TIME_OUT = 3000;
        static final Random r = new Random();
        //锁的超时时间（秒），过期删除
        static final int EXPIRE = 60 * 60;
    }

    public static enum Table {
        CALLBACK("callback:%s"),
        CARDINFO("cardinfo:%s"),
        BINDOPT("bindopt:%s"),
        BINDCARD("bindcard:%s"),
        CONFIRM("confirm:%s"),
        CONFIRM_LOCK("confirm_lock:%s"),
        ORDER("order:%s"),
        PREPAY("prepay:%s"),
        PREORDER("preorder:%s"),
        ALIPAY_QRCODE("alipay_qrcode:%s"),
        PAY_AMOUNT("amount:%s"),
        PAY_DATA("data:%s"),

        REPLAY("replay:%s");

        // 成员变量
        private String name;

        // 构造方法
        private Table(String name) {
            this.name = name;
        }

        public String getKey(String key) {
            return String.format(name, key);
        }

    }
}
