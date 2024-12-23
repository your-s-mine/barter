package com.barter.config;

import java.util.Collection;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import jakarta.validation.constraints.NotNull;

public class CustomCacheResolver implements CacheResolver {

	private final Map<String, CacheManager> cacheManagerMap;

	public CustomCacheResolver(CacheManager suggestionListCacheManager, CacheManager immediateTradeListCacheManager) {
		this.cacheManagerMap = Map.of(
			"suggestionList", suggestionListCacheManager,
			"immediateTradeList", immediateTradeListCacheManager
		);
	}

	@Override
	@NotNull
	public Collection<? extends Cache> resolveCaches(@NotNull CacheOperationInvocationContext<?> context) {

		String cacheName = context.getOperation().getCacheNames().iterator().next();
		CacheManager cacheManager = cacheManagerMap.get(cacheName);
		if (cacheManager == null) {
			throw new IllegalArgumentException("캐시 오류: " + cacheName);
		}

		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			throw new IllegalArgumentException("캐시를 찾을 수 없습니다: " + cacheName);
		}

		return cacheManager.getCacheNames()
			.stream()
			.filter(name -> name.equals(cacheName))
			.map(cacheManager::getCache)
			.toList();
	}
}
