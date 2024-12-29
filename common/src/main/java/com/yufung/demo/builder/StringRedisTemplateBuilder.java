package com.yufung.demo.builder;

import com.yufung.demo.configure.IRedisBuilder;
import com.yufung.demo.configure.JedisSetting;
import com.yufung.demo.configure.RedisConnectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * string类型的redis创建者
 * @author kingapex
 * @version v1.0
 * @since v0.0.1
 * 2022年3月23日 上午10:26:41
 */
@Component
public class StringRedisTemplateBuilder {
	private static Logger logger = LoggerFactory.getLogger(StringRedisTemplateBuilder.class);

	@Autowired
	private List<IRedisBuilder> redisBuilder;

	@Autowired
	private RedisConnectionConfig config;

	/**
	 * 构建锁
	 */
	private static final Lock LOCK = new ReentrantLock();

	public StringRedisTemplate build() {

		StringRedisTemplate redisTemplate = null;
		
		JedisSetting.loadPoolConfig(config);
		
		while (true) {
			try {
				LOCK.tryLock(10, TimeUnit.MILLISECONDS);
				if (redisTemplate == null) {

					IRedisBuilder redisBuilder = this.getRedisBuilder();
					LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisBuilder.buildConnectionFactory( config);
					// 初始化连接pool
					lettuceConnectionFactory.afterPropertiesSet();
					redisTemplate = new StringRedisTemplate();
					redisTemplate.setConnectionFactory(lettuceConnectionFactory);
					return redisTemplate;
				}
			} catch (Exception e) {
				// 容错
				logger.error(e.getMessage(), e);
				break;
			} finally {
				LOCK.unlock();
			}
			try {
				// 活锁
				TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(1000));
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return redisTemplate;
	}

	private IRedisBuilder getRedisBuilder() {
		for (IRedisBuilder builder : redisBuilder) {
			if (builder.getType().name().equals(config.getType())) {
				return builder;
			}
		}
		throw new RuntimeException("{\"cn\":\"没有找到对应的配置方式\", \"en\":\"No corresponding configuration method was found\"}");
	}
}
