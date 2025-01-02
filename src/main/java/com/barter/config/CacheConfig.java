package com.barter.config;

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

@Configuration
@EnableCaching
public class CacheConfig {

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
		cacheManager.setCaffeine(caffeineCacheBuilder());
		return cacheManager;
	}

	@Bean
	public CacheManager searchCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCacheNames(Collections.singletonList("searchResults"));
		cacheManager.setCaffeine(caffeineCacheBuilder());
		return cacheManager;
	}

	@Bean
	public CacheResolver cacheResolver(
		@Qualifier("suggestionListCacheManager") CacheManager suggestionListCacheManager,
		@Qualifier("immediateTradeListCacheManager") CacheManager immediateTradeListCacheManager,
		@Qualifier("searchCacheManager") CacheManager searchListCacheManager) {
		return new CustomCacheResolver(suggestionListCacheManager, immediateTradeListCacheManager, searchListCacheManager);
	}

	private Caffeine<Object, Object> caffeineCacheBuilder() {
		return Caffeine.newBuilder()
			.expireAfterWrite(24, TimeUnit.HOURS)
			.maximumSize(10000);
	}
}
