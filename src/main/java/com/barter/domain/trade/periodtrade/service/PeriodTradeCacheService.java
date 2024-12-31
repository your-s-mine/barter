package com.barter.domain.trade.periodtrade.service;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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

	// @Scheduled(fixedRate = 6000000) // 1분 마다 캐시 업데이트
	// public void updateCache() {
	//
	// 	List<FindPeriodTradeResDto> periodTradeResDtos = periodTradeRepository.findAll()
	// 		.stream()
	// 		.map(FindPeriodTradeResDto::from)
	// 		.toList();
	//
	// 	// TODO : 수정 필요 위 코드에서 현재는 1분마다 거의 더미데이터 30만건에 달하는 데이터를 캐시에 저장하고 있음 (이는 서버에 문제가 발생할 가능성도 있다.)
	//
	// 	log.info("기간 교환 캐시 업데이트 진행 {}", LocalDateTime.now());
	// 	log.info("캐싱 정보 {}", periodTradeResDtos);
	// 	//redisTemplate.opsForValue().set(PERIOD_TRADE_CACHE_KEY, periodTradeResDtos);
	// }

	// 캐시에서 데이터 조회
	public Page<FindPeriodTradeResDto> getPeriodTradesFromCache(Pageable pageable) {
		System.out.println("기간 교환 캐시 도입 부");

		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		String cacheKey = PERIOD_TRADE_CACHE_KEY + "_PAGE_" + pageNumber + "_SIZE_" + pageSize;
		System.out.println("cacheKey = " + cacheKey);

		List<FindPeriodTradeResDto> cachedTrades = redisTemplate.opsForValue()
			.get(cacheKey);

		System.out.println("cachedTrades = " + cachedTrades);
		System.out.println("pageable = " + pageable.getPageNumber());

		if (cachedTrades == null) {
			System.out.println("if 문 실행");
			Page<PeriodTrade> periodTradePage = periodTradeRepository.findAll(pageable);
			System.out.println("periodTradePage = " + periodTradePage);

			cachedTrades = periodTradePage.getContent()
				.stream()
				.map(FindPeriodTradeResDto::from)
				.toList();

			redisTemplate.opsForValue().set(cacheKey, cachedTrades, Duration.ofMinutes(10));

		}

		int start = 0;
		int end = Math.min(start + pageSize, cachedTrades.size());
		System.out.println("start = " + start);
		System.out.println("end = " + end);

		List<FindPeriodTradeResDto> pagedTrades = cachedTrades.subList(start, end);

		System.out.println("cachedTrades.size() = " + cachedTrades.size());
		System.out.println("pageable = " + pageable);

		return new PageImpl<>(pagedTrades, pageable, countTotalTrades());
	}

	private long countTotalTrades() {
		return periodTradeRepository.count();
	}

}
