package com.barter.domain.trade.periodtrade.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeCustomRepositoryImpl;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeriodTradeCacheService {

	private final PeriodTradeRepository periodTradeRepository;
	private final PeriodTradeCustomRepositoryImpl periodTradeCustomRepository;

	@Qualifier("periodTradeRedisTemplate")
	private final RedisTemplate<String, List<FindPeriodTradeResDto>> redisTemplate;

	@Qualifier("periodTradeCountRedisTemplate")
	private final RedisTemplate<String, Long> periodTradeLongRedisTemplate;

	public PeriodTradeCacheService(PeriodTradeRepository periodTradeRepository,
		PeriodTradeCustomRepositoryImpl periodTradeCustomRepository,
		RedisTemplate<String, List<FindPeriodTradeResDto>> redisTemplate,
		RedisTemplate<String, Long> periodTradeCountRedisTemplate) {
		this.periodTradeRepository = periodTradeRepository;
		this.redisTemplate = redisTemplate;
		this.periodTradeCustomRepository = periodTradeCustomRepository;
		this.periodTradeLongRedisTemplate = periodTradeCountRedisTemplate;

	}

	// 조회 수 -------------------------------------------------------------

	private static final String PERIOD_TRADE_CACHE_KEY = "periodTrades";

	// 조회 수 증가
	public void addViewCount(PeriodTrade periodTrade) {
		String cacheKey = PERIOD_TRADE_CACHE_KEY + "_VIEWCOUNT_" + periodTrade.getId();
		periodTradeLongRedisTemplate.opsForValue().increment(cacheKey, 1);

	}

	// 조회 수 가져오기
	public Long getViewCount(Long periodTradeId) {
		String cacheKey = PERIOD_TRADE_CACHE_KEY + "_VIEWCOUNT_" + periodTradeId;
		Long viewCount = periodTradeLongRedisTemplate.opsForValue().get(cacheKey);
		return viewCount != null ? viewCount : 0L;
	}

	// 조회 수 Key 값 조회
	public Set<String> getViewCountKeys() {
		return periodTradeLongRedisTemplate.keys(PERIOD_TRADE_CACHE_KEY + "_VIEWCOUNT_*");
	}

	// 캐시에서 삭제
	public void deleteCachedViewCount(Long periodTradeId) {
		String cacheKey = PERIOD_TRADE_CACHE_KEY + "_VIEWCOUNT_" + periodTradeId;
		periodTradeLongRedisTemplate.delete(cacheKey);
	}

	// 기간 교환 조회 ---------------------------------------------------------
	// 캐시에서 데이터 조회
	public Page<FindPeriodTradeResDto> getPeriodTradesFromCache(Pageable pageable) {

		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		String cacheKey = PERIOD_TRADE_CACHE_KEY + "_PAGE_" + pageNumber + "_SIZE_" + pageSize;

		List<FindPeriodTradeResDto> cachedTrades = redisTemplate.opsForValue()
			.get(cacheKey);

		if (cachedTrades == null) { // 첫 페이지에만 적용해보기
			List<PeriodTrade> periodTradePage = periodTradeCustomRepository.paginationCoveringIndex(
				pageable);

			System.out.println("-----------> offset : " + pageNumber * pageSize);

			System.out.println("periodTradePage = " + periodTradePage);

			cachedTrades = periodTradePage.stream()
				.map(FindPeriodTradeResDto::from)
				.toList();

			if (pageNumber == 0) { // 0페이지만 캐싱 적용 (디폴트 값 updatedAt 순서이므로)
				redisTemplate.opsForValue().set(cacheKey, cachedTrades, Duration.ofMinutes(10));
			}
		}

		int start = 0;
		int end = Math.min(start + pageSize, cachedTrades.size());

		List<FindPeriodTradeResDto> pagedTrades = cachedTrades.subList(start, end);

		return new PageImpl<>(pagedTrades, pageable, countTotalTrades());
	}

	private static final String TOTAL_TRADES_COUNT_CACHE_KEY = "totalTradesCount";

	private long countTotalTrades() {
		Long cachedCount = periodTradeLongRedisTemplate.opsForValue().get(TOTAL_TRADES_COUNT_CACHE_KEY);

		if (cachedCount == null) {
			long totalTradesCount = periodTradeRepository.count();
			periodTradeLongRedisTemplate.opsForValue()
				.set(TOTAL_TRADES_COUNT_CACHE_KEY, totalTradesCount, Duration.ofMinutes(10));
			return totalTradesCount;
		}

		return cachedCount;
	}

}
