package com.barter.config;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {
	@Configuration
	@EnableCaching // 캐싱 활성화
	public class RedisCacheConfig {

		@Bean
		public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
			RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
					.entryTtl(Duration.ofMinutes(10)) // 캐시 만료 시간 10분
					.disableCachingNullValues() // Null 값 캐싱 방지
					.serializeKeysWith(
							RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
					);
			return org.springframework.data.redis.cache.RedisCacheManager.builder(redisConnectionFactory)
					.cacheDefaults(config)
					.build();
		}

		@Bean
		@Primary
		public CacheManager suggestionListCacheManager() {
			ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
			cacheManager.setCacheNames(Collections.singletonList("suggestionList"));
			return cacheManager;
		}

		@Bean
		public CacheManager immediateTradeListCacheManager() {
			CaffeineCacheManager cacheManager = new CaffeineCacheManager();
			cacheManager.setCacheNames(Collections.singletonList("immediateTradeList"));
			cacheManager.setCaffeine(Caffeine.newBuilder()
					.expireAfterWrite(24, TimeUnit.HOURS)
					.maximumSize(10000));
			return cacheManager;
		}

		@Bean
		public CacheResolver cacheResolver(
				@Qualifier("suggestionListCacheManager") CacheManager suggestionListCacheManager,
				@Qualifier("immediateTradeListCacheManager") CacheManager immediateTradeListCacheManager) {
			return new CustomCacheResolver(suggestionListCacheManager, immediateTradeListCacheManager);
		}
	}
}