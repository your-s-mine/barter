package com.barter.event.trade.PeriodTradeEvent;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;
import com.barter.domain.trade.periodtrade.service.PeriodTradeCacheService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PeriodTradeViewCountPersistenceScheduler {
	private final PeriodTradeCacheService periodTradeCacheService;
	private final PeriodTradeRepository periodTradeRepository;

	@Scheduled(fixedRate = 60000)
	@Transactional
	public void persistPeriodTradeViewCount() {
		Set<String> keys = periodTradeCacheService.getViewCountKeys();

		for (String key : keys) {
			Long periodTradeId = Long.valueOf(key.replace("periodTrades_VIEWCOUNT_", ""));
			Long viewCount = periodTradeCacheService.getViewCount(periodTradeId);

			if (viewCount != null) {
				periodTradeRepository.updateViewCount(periodTradeId, viewCount);
				periodTradeCacheService.deleteCachedViewCount(periodTradeId);
			}

		}
	}
}
