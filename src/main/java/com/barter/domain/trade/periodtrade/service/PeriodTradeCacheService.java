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

	public PeriodTradeCacheService(PeriodTradeRepository periodTradeRepository,
		PeriodTradeCustomRepositoryImpl periodTradeCustomRepository,
		RedisTemplate<String, List<FindPeriodTradeResDto>> redisTemplate) {
		this.periodTradeRepository = periodTradeRepository;
		this.redisTemplate = redisTemplate;
		this.periodTradeCustomRepository = periodTradeCustomRepository;

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
	//
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

		if (cachedTrades == null) { // 첫 페이지에만 적용해보기
			System.out.println("if 문 실행");
			List<PeriodTrade> periodTradePage = periodTradeCustomRepository.paginationCoveringIndex(
				pageable);

			System.out.println("-----------> offset : " + pageNumber * pageSize);

			System.out.println("periodTradePage = " + periodTradePage);

			cachedTrades = periodTradePage.stream()
				.map(FindPeriodTradeResDto::from)
				.toList();

			if (pageNumber == 0) { // 1페이지만 캐싱 적용 (디폴트 값 updatedAt 순서이므로)
				redisTemplate.opsForValue().set(cacheKey, cachedTrades, Duration.ofMinutes(10));
			}
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
		//TODO :  캐싱 적용 필요 (위 count 자체가 db에서 계속 불러오는 것이다.)
	}

}
