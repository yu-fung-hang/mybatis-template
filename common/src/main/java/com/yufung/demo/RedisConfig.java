package com.yufung.demo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yufung.demo.builder.RedisTemplateBuilder;
import com.yufung.demo.builder.StringRedisTemplateBuilder;
import com.yufung.demo.configure.RedisConnectionConfig;
import com.yufung.demo.configure.RedisType;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;

/**
 * Redis配置
 * @author kingapex
 * 2017年8月2日上午11:52:50
 *
 * 修改文件位置,增加RedissonClinet的配置
 * @version 2.0
 * @since 6.4
 */
@Configuration
@EnableCaching //开启redis缓存
public class RedisConfig extends CachingConfigurerSupport {

	@Autowired
	private RedisTemplateBuilder redisTemplateBuilder;

	@Autowired
	private StringRedisTemplateBuilder stringRedisTemplateBuilder;

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {

		RedisTemplate<String,Object> redisTemplate = null;

		redisTemplate = redisTemplateBuilder.build();

		return redisTemplate;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate() {

		StringRedisTemplate redisTemplate = null;

		redisTemplate = stringRedisTemplateBuilder.build();

		return redisTemplate;
	}

	@Bean
	LettuceConnectionFactory lettuceConnectionFactory() {
		return (LettuceConnectionFactory) redisTemplate().getConnectionFactory();
	}


	@Bean
	public RedissonClient redissonClient(LettuceConnectionFactory lettuceConnectionFactory, RedisConnectionConfig config) {
 		Config rconfig = null;
		String type  = config.getType();


		//独立模式
		if( RedisType.standalone.name().equals(type) ){
			rconfig = new Config();
			RedisStandaloneConfiguration standaloneConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
			String host  = standaloneConfiguration.getHostName();
			int port = standaloneConfiguration.getPort();
			SingleServerConfig singleServerConfig =  rconfig.useSingleServer().setAddress("redis://" + host+":" + port);
			if(standaloneConfiguration.getPassword().isPresent()){
				String password  = new String(standaloneConfiguration.getPassword().get() );
				singleServerConfig.setPassword(password);
			}

		}


		//哨兵模式
		if( RedisType.sentinel.name().equals(type) ){
			rconfig = new Config();
			RedisSentinelConfiguration sentinelConfiguration =  lettuceConnectionFactory.getSentinelConfiguration();
			String masterName  =  sentinelConfiguration.getMaster().getName();
			Set<RedisNode> nodeSet =sentinelConfiguration.getSentinels();

			SentinelServersConfig sentinelServersConfig = rconfig.useSentinelServers().setMasterName(masterName);

			for (RedisNode node : nodeSet){
				sentinelServersConfig.addSentinelAddress("redis://"+node.asString());

			}

			//添加密码
			if(sentinelConfiguration.getPassword().isPresent()){
				String password  = new String(sentinelConfiguration.getPassword().get() );
				sentinelServersConfig.setPassword(password);
			}


		}

		//集群模式
		if( RedisType.cluster.name().equals(type) ){
			rconfig = new Config();
			RedisClusterConfiguration clusterConfiguration =  lettuceConnectionFactory.getClusterConfiguration();
			Set<RedisNode> nodeSet = clusterConfiguration.getClusterNodes();
			ClusterServersConfig clusterServersConfig =  rconfig.useClusterServers();
			for (RedisNode node : nodeSet){
				clusterServersConfig.addNodeAddress("redis://"+node.asString());
			}
			//添加密码
			if (clusterConfiguration.getPassword().isPresent()) {
				String password = new String(clusterConfiguration.getPassword().get());
				clusterServersConfig.setPassword(password);
			}
		}

		if(  rconfig == null){
			throw  new RuntimeException("{\"cn\":\"错误的redis 类型，请检查 redis.type参数\", \"en\":\"Wrong redis type, please check the redis.type parameter\"}");
		}
		RedissonClient redisson = Redisson.create(rconfig);
		return  redisson;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		// “enabled” deserialization 与 security 结合时，反序列化时的配置
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(om, Object.class);
		template.setConnectionFactory(factory);
		//key序列化方式
		template.setKeySerializer(redisSerializer);
		//value序列化
		template.setValueSerializer(jackson2JsonRedisSerializer);
		//value hashmap序列化
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();
		//解决查询缓存转换异常的问题
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		// “enabled” deserialization
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(om, Object.class);
		// 配置序列化（解决乱码的问题）,过期时间600秒
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(600))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
				.disableCachingNullValues();
		RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
				.cacheDefaults(config)
				.build();
		return cacheManager;
	}
}
