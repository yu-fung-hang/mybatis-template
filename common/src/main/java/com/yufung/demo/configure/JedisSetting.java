package com.yufung.demo.configure;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 初始化redi使用
 * @author manfred
 * @version v1.0
 * @since v0.0.1
 * 2022年3月23日 上午10:26:41
 */
public class JedisSetting {
	
	private static JedisPoolConfig poolConfig;
	
	private static  RedisConnectionConfig connectionConfig;

	public static void loadPoolConfig(RedisConnectionConfig config) {
		
		poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(config.getMaxTotal()==null?(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL * 3):config.getMaxTotal());
		poolConfig.setMaxIdle(config.getMaxIdle()==null?(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 2):config.getMaxIdle());
		poolConfig.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE);
		poolConfig.setMaxWaitMillis(config.getMaxWaitMillis()==null?1000L:config.getMaxWaitMillis());
		poolConfig.setJmxNamePrefix("preka-redis-pool");
		poolConfig.setJmxEnabled(true);
		poolConfig.setTestOnBorrow(true);
		
		connectionConfig = config;
		
	}
	
	public static JedisPoolConfig getPoolConfig(){
		
		return poolConfig;
	}

	public static RedisConnectionConfig getConnectionConfig() {
		
		return connectionConfig;
	}
	
}
