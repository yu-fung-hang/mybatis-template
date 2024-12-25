package com.yufung.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MybatisRedisCache implements org.apache.ibatis.cache.Cache {

    /**
     * 读写锁
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * 操作缓存template
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 唯一id
     */
    private String id;

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    /**
     * 缓存实现构造器
     *
     * @param id 唯一id
     */
    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("{\"cn\":\"缓存实例需要一个 ID\", \"en\":\"Cache instances require an ID\"}");
        }
        this.id = id;
    }

    /**
     * 获取唯一id
     *
     * @return
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 像缓存中存入一个对象
     *
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        logger.debug("存入key："+id+"--"+ key);
        if (value != null) {
            getRedisTemplate().opsForHash().put(id,key,value);
//            getRedisTemplate().opsForValue().set(getKey(key), value);
        }
    }


    /**
     * 由缓存中读取对象
     *
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {
        logger.debug("获取key："+id+"--"+ key);
        try {
            if (key != null) {
                Object obj =getRedisTemplate().opsForHash().get(id, key);
                //System.out.println("cache + "+obj);
//                Object obj = getRedisTemplate().opsForValue().get(getKey(key));
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 移除一个对象,这个方法只有在发生回滚是调用。
     * 在删除是不会调用，目的是如果发生了删除且缓存过，则查不用读库
     * As of 3.3.0 this method is only called during a rollback
     * for any previous value that was missing in the cache.
     * This lets any blocking cache to release the lock that
     * may have previously put on the key.
     * A blocking cache puts a lock when a value is null
     * and releases it when the value is back again.
     * This way other threads will wait for the value to be
     * available instead of hitting the database.
     *
     * @param key
     * @return
     */
    @Override
    public Object removeObject(Object key) {

        logger.debug("移除key："+id+"--"+ key);

        if (key != null) {
            getRedisTemplate().opsForHash().delete(id, key);
        }
        return null;
    }

    @Override
    public void clear() {
        logger.debug("移除相关的的key-->" + this.id + "*");

        getRedisTemplate().opsForHash().getOperations().delete(id);

    }

    @Override
    public int getSize() {

        Long size = getRedisTemplate().execute((RedisCallback<Long>) RedisServerCommands::dbSize);
        logger.debug("获取大小："+size.intValue());
        return size.intValue();
    }

    /**
     * 为mybatis提供读写锁
     *
     * @return
     */
    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }


    private RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate<String, Object>) ApplicationContextHolder.getBean("redisTemplate");
        }
        return redisTemplate;
    }


}

