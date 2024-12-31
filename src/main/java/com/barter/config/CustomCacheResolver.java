package com.barter.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import jakarta.validation.constraints.NotNull;

public class CustomCacheResolver implements CacheResolver {
	private final Map<String, CacheManager> cacheManagerMap;

	public CustomCacheResolver(CacheManager suggestionListCacheManager, CacheManager immediateTradeListCacheManager) {
		this.cacheManagerMap = new HashMap<>();
		cacheManagerMap.put("suggestionList", suggestionListCacheManager);
		cacheManagerMap.put("immediateTradeList", immediateTradeListCacheManager);
	}

	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		String cacheName = context.getOperation().getCacheNames().iterator().next();
		CacheManager cacheManager = cacheManagerMap.get(cacheName);

		if (cacheManager == null) {
			throw new IllegalArgumentException("Unknown cache name: " + cacheName);
		}

		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			// 캐시가 없으면 생성
			if (cacheManager instanceof ConcurrentMapCacheManager) {
				((ConcurrentMapCacheManager) cacheManager).setCacheNames(Arrays.asList(cacheName));
			} else if (cacheManager instanceof CaffeineCacheManager) {
				((CaffeineCacheManager) cacheManager).setCacheNames(Arrays.asList(cacheName));
			}
			cache = cacheManager.getCache(cacheName);
		}

		return Collections.singletonList(cache);
	}
}