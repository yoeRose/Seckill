package com.yoe.service;

import com.alibaba.fastjson.JSON;
import com.yoe.redis.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;


    /**
     * 获取缓存
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix ,String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = StringToBean(str,clazz);
            return t;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }


    /**
     * 添加缓存
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix,String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String strValue = beanToString(value);
            if(strValue == null || strValue.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            //获取key的过期时间
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){
                jedis.set(realKey,strValue);
            }else{
                jedis.setex(realKey,seconds,strValue);
            }
            return true;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 判断缓存中的key是否存在
     * @param prefix
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 删除缓存
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            Long result = jedis.del(realKey);
            return result > 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 增加key对应value的一个值
     * @param prefix
     * @param key
     * @param <T>
     * @return 返回影响的key条数
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 减少key对应value的一个值
     * @param prefix
     * @param key
     * @param <T>
     * @return 返回影响的key条数
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 序列化
     * @param value
     * @param <T>
     * @return
     */
    public <T> String beanToString(T value){
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return String.valueOf(value);
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return String.valueOf(value);
        }else{
            return JSON.toJSONString(value);
        }
    }

    /**
     * 反序列化
     * @param str
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public  <T> T StringToBean(String str,Class<T> clazz){
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }


}
