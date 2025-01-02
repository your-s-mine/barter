package com.barter.domain.search.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.search.dto.ConvertRegisteredProductDto;
import com.barter.domain.search.dto.SearchTradeReqDto;
import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.util.DistanceCalculator;
import com.barter.domain.trade.TradeCommonEntity;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;
import com.barter.exception.customexceptions.SearchException;
import com.barter.exception.enums.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

	private final DonationTradeRepository donationTradeRepository;
	private final ImmediateTradeRepository immediateTradeRepository;
	private final PeriodTradeRepository periodTradeRepository;
	private final DistanceCalculator distanceCalculator;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String SEARCH_BUCKET_PREFIX = "search_bucket:";
	private static final int BUCKET_SIZE_MINUTES = 60;

	@CachePut(cacheNames = "searchResults", key = "#word + '-' + #reqDto.address1", cacheManager = "searchCacheManager")
	@Transactional
	public List<SearchTradeResDto> searchKeywordAndFindTrades(String word, SearchTradeReqDto reqDto) {
		long currentTime = System.currentTimeMillis() / 1000L;
		String location = reqDto.getAddress1();

		// 현재 시간이 속한 버킷 키 생성
		String currentBucketKey = generateBucketKey(location, currentTime);

		redisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			@SuppressWarnings("unchecked")
			public List<Object> execute(RedisOperations operations) {
				operations.multi();
				// 현재 시간 버킷의 카운트 증가
				operations.opsForZSet().incrementScore(currentBucketKey, word, 1);
				// 버킷의 만료시간 설정 (25시간으로 설정하여 겹치는 시간 처리)
				operations.expire(currentBucketKey, Duration.ofHours(25));
				return operations.exec();
			}
		});

		CompletableFuture<List<ImmediateTrade>> immediateTradesFuture = findImmediateTradesAsync(word,
			reqDto.getAddress1());
		CompletableFuture<List<PeriodTrade>> periodTradesFuture = findPeriodTradesAsync(word, reqDto.getAddress1());

		List<SearchTradeResDto> tradeDtos = new ArrayList<>();
		try {
			List<ImmediateTrade> immediateTrades = immediateTradesFuture.get(5, TimeUnit.SECONDS);
			List<PeriodTrade> periodTrades = periodTradesFuture.get(5, TimeUnit.SECONDS);

			tradeDtos.addAll(mapTradesToSearchTradeRes(immediateTrades, reqDto.getLongitude(), reqDto.getLatitude()));
			tradeDtos.addAll(mapTradesToSearchTradeRes(periodTrades, reqDto.getLongitude(), reqDto.getLatitude()));
		} catch (Exception e) {
			log.error("Error fetching trade results: ", e);
			throw new SearchException(ExceptionCode.FAIL_TO_FETCH_TRADE_RESULT);
		}

		if (tradeDtos.isEmpty()) {
			tradeDtos.add(SearchTradeResDto.builder()
				.title("검색 결과가 없습니다.")
				.build());
		}

		return tradeDtos;
	}

	public List<String> findPopularKeywords(String location) {
		long currentTime = System.currentTimeMillis() / 1000L;

		// 최근 24시간의 모든 버킷 키 조회
		Set<String> bucketKeys = new HashSet<>();
		for (int i = 0; i < 24; i++) {
			String bucketKey = generateBucketKey(location, currentTime - (i * 3600));
			bucketKeys.add(bucketKey);
		}

		// 모든 버킷의 검색어 집계
		Map<String, Double> aggregatedScores = new HashMap<>();
		for (String bucketKey : bucketKeys) {
			Set<ZSetOperations.TypedTuple<String>> bucketItems =
				redisTemplate.opsForZSet().rangeByScoreWithScores(bucketKey, 0, Double.MAX_VALUE);

			if (bucketItems != null) {
				for (ZSetOperations.TypedTuple<String> item : bucketItems) {
					String word = item.getValue();
					Double score = item.getScore();
					aggregatedScores.merge(word, score, Double::sum);
				}
			}
		}

		// 상위 10개 추출
		return aggregatedScores.entrySet().stream()
			.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
			.limit(10)
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());
	}

	private String generateBucketKey(String location, long timestamp) {
		// 시간을 1시간 단위로 버킷팅
		long bucket = timestamp / (BUCKET_SIZE_MINUTES * 60);
		return SEARCH_BUCKET_PREFIX + location + ":" + bucket;
	}

	@Async
	public CompletableFuture<List<ImmediateTrade>> findImmediateTradesAsync(String word, String address1) {
		return CompletableFuture.completedFuture(
			immediateTradeRepository.findImmediateTradesWithProduct(word, address1));
	}

	@Async
	public CompletableFuture<List<PeriodTrade>> findPeriodTradesAsync(String word, String address1) {
		return CompletableFuture.completedFuture(periodTradeRepository.findPeriodTradesWithProduct(word, address1));
	}

	private List<SearchTradeResDto> mapTradesToSearchTradeRes(List<? extends TradeCommonEntity> trades,
		Double memberLongitude, Double memberLatitude) {
		return trades.stream()
			.map(trade -> SearchTradeResDto.builder()
				.title(trade.getTitle())
				.product(createConvertedProductDto(trade.getRegisteredProduct()))
				.tradeStatus(trade.getStatus())
				.viewCount(trade.getViewCount())
				.distance(distanceCalculator.calculateDistance(
					memberLatitude, memberLongitude,
					trade.getLatitude(), trade.getLongitude()))
				.build())
			.collect(Collectors.toList());
	}

	private ConvertRegisteredProductDto createConvertedProductDto(RegisteredProduct product) {
		return ConvertRegisteredProductDto.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.images(product.getImages())
			.status(product.getStatus())
			.build();
	}
}
