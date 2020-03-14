package com.xbc.community.productSeckill.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * 创建者 科帮网
 * 创建时间	2018年4月8日
 */
@Component
public class RedisUtil {

   private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
	private RedisTemplate<Serializable, Serializable> redisTemplate;

	/**
     * 前缀
     */
   // public static  String KEY_PREFIX_VALUE = "seckill:value:";


	/**
     * 缓存value操作
     * @param k
     * @param v
     * @param time
     * @return
     */
    public  boolean cacheValue(String k, Serializable v, long time) {
        String key =  k;
       // System.out.println(key);
       // System.out.println(2);
        try {
            ValueOperations<Serializable, Serializable> valueOps =  redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            logger.error("缓存[{}]失败, value[{}]",key,v,t);
        }
        return false;
    }
    /**
     * 缓存value操作
     * @Author  科帮网
     * @param k
     * @param v
     * @param time
     * @param unit
     * @return  boolean
     * @Date	2017年12月23日
     * 更新日志
     * 2017年12月23日  科帮网  首次创建
     *
     */
    public  boolean cacheValue(String k, Serializable v, long time,TimeUnit unit) {
        String key = k;
        try {
            ValueOperations<Serializable, Serializable> valueOps =  redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, unit);
            return true;
        } catch (Throwable t) {
            logger.error("缓存[{}]失败, value[{}]",key,v,t);
        }
        return false;
    }

    /**
     * 缓存value操作
     * @param k
     * @param v
     * @return
     */
    public  boolean cacheValue(String k, Serializable v) {
        return cacheValue(k, v, -1);
    }

    /**
     * 判断缓存是否存在
     * @param k
     * @return
     */
    public  boolean containsValueKey(String k) {
        String key =  k;
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            logger.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }
    /**
     * 获取缓存
     * @param k
     * @return
     */
    public  Object getValue(String k) {
        //System.out.println(k);
        try {
            return redisTemplate.opsForValue().get(k);
        } catch (Throwable t) {
            logger.error("获取缓存失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }
    /**
     * 移除缓存
     * @param k
     * @return
     */
    public  boolean removeValue(String k) {
    	String key =  k;
    	try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            logger.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }
    /**
     * 递增
     * @param k
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String k, long delta) {
        String key =  k;
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param k 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String k, long delta) {
        String key = k;
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
}
