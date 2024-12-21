package com.barter.domain.search.cache;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
@Component
@EnableScheduling
public class PopularKeywordScheduler {

	private final PopularKeywordCacheManager cacheManager;

	public PopularKeywordScheduler(PopularKeywordCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void updatePopularKeywords() {

		cacheManager.clearCache();

	}
}
*/