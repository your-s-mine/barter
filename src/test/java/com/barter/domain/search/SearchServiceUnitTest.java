package com.barter.domain.search;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.search.dto.SearchTradeReqDto;
import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.service.SearchService;
import com.barter.domain.search.util.DistanceCalculator;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

@ExtendWith(MockitoExtension.class)
public class SearchServiceUnitTest {
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ImmediateTradeRepository immediateTradeRepository;
	@Mock
	private PeriodTradeRepository periodTradeRepository;
	@Mock
	private DistanceCalculator distanceCalculator;
	@Mock
	private ZSetOperations<String, String> zSetOperations;
	@InjectMocks
	private SearchService searchService;

	private List<ImmediateTrade> immediateTrades = new ArrayList<>();
	private SearchTradeReqDto reqDto;
	private Double distance = 10.12;
	private static final String TEST_LOCATION = "상수동";

	@BeforeEach
	void setUp() {
		reqDto = SearchTradeReqDto.builder()
			.address1(TEST_LOCATION)
			.longitude(12.123)
			.latitude(40.123)
			.build();

		immediateTrades.add(
			ImmediateTrade.builder()
				.registeredProduct(RegisteredProduct.builder()
					.id(1L)
					.name("banana")
					.description("fresh banana")
					.status(RegisteredStatus.PENDING)
					.build())
				.title("Immediate banana")
				.latitude(12.1234)
				.longitude(40.546)
				.status(TradeStatus.PENDING)
				.viewCount(10)
				.build());

	}

	@Test
	@DisplayName("검색어 저장 및 검색 결과 조회")
	void searchKeywordAndFindTrades() {
		// Given
		String word = "banana";

		when(redisTemplate.execute(any(SessionCallback.class)))
			.thenReturn(Arrays.asList(1D, true));
		when(immediateTradeRepository.findImmediateTradesWithProduct(word, TEST_LOCATION))
			.thenReturn(immediateTrades);
		when(periodTradeRepository.findPeriodTradesWithProduct(word, TEST_LOCATION))
			.thenReturn(Collections.emptyList());
		when(distanceCalculator.calculateDistance(
			reqDto.getLatitude(), reqDto.getLongitude(),
			immediateTrades.get(0).getLatitude(),
			immediateTrades.get(0).getLongitude()))
			.thenReturn(distance);

		// When
		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word, reqDto);

		// Then
		assertThat(result).hasSize(1);
		SearchTradeResDto dto = result.get(0);
		assertThat(dto.getTitle()).isEqualTo("Immediate banana");
		assertThat(dto.getDistance()).isEqualTo(distance);
		assertThat(dto.getViewCount()).isEqualTo(10);
		assertThat(dto.getTradeStatus()).isEqualTo(TradeStatus.PENDING);

		verify(redisTemplate).execute(any(SessionCallback.class));
		verify(immediateTradeRepository).findImmediateTradesWithProduct(word, TEST_LOCATION);
		verify(periodTradeRepository).findPeriodTradesWithProduct(word, TEST_LOCATION);
	}

	@Test
	@DisplayName("검색 결과가 없는 경우")
	void searchedNothing() {
		// Given
		String word = "nonexistent";

		when(redisTemplate.execute(any(SessionCallback.class)))
			.thenReturn(Arrays.asList(1D, true));
		when(immediateTradeRepository.findImmediateTradesWithProduct(word, TEST_LOCATION))
			.thenReturn(Collections.emptyList());
		when(periodTradeRepository.findPeriodTradesWithProduct(word, TEST_LOCATION))
			.thenReturn(Collections.emptyList());

		// When
		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word, reqDto);

		// Then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("검색 결과가 없습니다.");

		verify(redisTemplate).execute(any(SessionCallback.class));
		verify(immediateTradeRepository).findImmediateTradesWithProduct(word, TEST_LOCATION);
		verify(periodTradeRepository).findPeriodTradesWithProduct(word, TEST_LOCATION);
	}

	@Test
	@DisplayName("인기 검색어 조회")
	void findPopularKeywords() {
		// Given
		Set<ZSetOperations.TypedTuple<String>> mockTuples = new HashSet<>();
		mockTuples.add(new DefaultTypedTuple<>("banana", 5.0));
		mockTuples.add(new DefaultTypedTuple<>("apple", 3.0));

		when(zSetOperations.rangeByScoreWithScores(anyString(), anyDouble(), anyDouble()))
			.thenReturn(mockTuples);
		when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);


		// When
		List<String> result = searchService.findPopularKeywords(TEST_LOCATION);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly("banana", "apple");

		verify(zSetOperations, times(24)).rangeByScoreWithScores(anyString(), anyDouble(), anyDouble());
	}
}