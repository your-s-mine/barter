package com.barter.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	@Primary
	public CacheManager suggestionListCacheManager() {
		return new ConcurrentMapCacheManager("suggestionList");
	}

	@Bean
	public CacheManager immediateTradeListCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("immediateTradeList");
		cacheManager.setCaffeine(Caffeine.newBuilder()
			.expireAfterWrite(24, TimeUnit.HOURS)
			.maximumSize(10000));
		return cacheManager;
	}

	@Bean
	public CacheResolver cacheResolver(CacheManager suggestionListCacheManager,
		CacheManager immediateTradeListCacheManager) {
		return new CustomCacheResolver(suggestionListCacheManager, immediateTradeListCacheManager);
	}
}
