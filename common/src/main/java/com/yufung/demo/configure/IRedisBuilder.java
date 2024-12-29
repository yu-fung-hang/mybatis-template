package com.yufung.demo.configure;

import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * redis构建接口
 * @author manfred
 * @version v6.4
 * @since v6.4
 * 2017年10月27日 下午2:05:40
 */
public interface IRedisBuilder {

	/**
	 * 构建连接
	 * @param  config redis配置
	 * @return
	 */
	RedisConnectionFactory buildConnectionFactory(RedisConnectionConfig config);
	
	/**
	 * 类型
	 * @return
	 */
    RedisType getType();
}