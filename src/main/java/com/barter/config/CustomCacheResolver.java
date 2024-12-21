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

	public CustomCacheResolver(CacheManager suggestionListCacheManager, CacheManager searchResultCacheManager) {
		this.cacheManagerMap = Map.of(
			"suggestionList", suggestionListCacheManager,
			"searchResult", searchResultCacheManager
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

		return cacheManager.getCacheNames()
			.stream()
			.filter(name -> name.equals(cacheName))
			.map(cacheManager::getCache)
			.toList();
	}
}
