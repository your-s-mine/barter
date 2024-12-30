package com.barter.domain.trade.periodtrade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeriodTradeCacheService {

	private final PeriodTradeRepository periodTradeRepository;

	@Qualifier("periodTradeRedisTemplate")
	private final RedisTemplate<String, List<FindPeriodTradeResDto>> redisTemplate;

	public PeriodTradeCacheService(PeriodTradeRepository periodTradeRepository,
		RedisTemplate<String, List<FindPeriodTradeResDto>> redisTemplate) {
		this.periodTradeRepository = periodTradeRepository;
		this.redisTemplate = redisTemplate;

	}

	private static final String PERIOD_TRADE_CACHE_KEY = "periodTrades";

	@Scheduled(fixedRate = 60000) // 1분 마다 캐시 업데이트

	public void updateCache() {

		List<FindPeriodTradeResDto> periodTradeResDtos = periodTradeRepository.findAll()
			.stream()
			.map(FindPeriodTradeResDto::from)
			.toList();

		redisTemplate.opsForValue().set(PERIOD_TRADE_CACHE_KEY, periodTradeResDtos);
	}

	// 캐시에서 데이터 조회
	public Page<FindPeriodTradeResDto> getPeriodTradesFromCache(Pageable pageable) {
		System.out.println("기간 교환 캐시 도입 부");

		List<FindPeriodTradeResDto> cachedTrades = redisTemplate.opsForValue()
			.get(PERIOD_TRADE_CACHE_KEY + "_PAGE_" + pageable.getPageNumber());

		if (cachedTrades == null) {
			Page<PeriodTrade> periodTradePage = periodTradeRepository.findAll(pageable);

			cachedTrades = periodTradePage.getContent()
				.stream()
				.map(FindPeriodTradeResDto::from)
				.toList();

			redisTemplate.opsForValue().set(PERIOD_TRADE_CACHE_KEY + "_PAGE_" + pageable.getPageNumber(), cachedTrades);

		}

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), cachedTrades.size());
		List<FindPeriodTradeResDto> pagedTrades = cachedTrades.subList(start, end);
		return new PageImpl<>(pagedTrades, pageable, cachedTrades.size());
	}

}
