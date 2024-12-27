package com.barter.domain.search;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.member.entity.Address;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.search.dto.SearchTradeReqDto;
import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.entity.SearchKeyword;
import com.barter.domain.search.repository.SearchHistoryRepository;
import com.barter.domain.search.repository.SearchKeywordRepository;
import com.barter.domain.search.service.SearchService;
import com.barter.domain.search.util.DistanceCalculator;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

@ExtendWith(MockitoExtension.class)
public class SearchServiceUnitTest {

	@Mock
	SearchKeywordRepository searchKeywordRepository;
	@Mock
	SearchHistoryRepository searchHistoryRepository;
	@Mock
	DonationTradeRepository donationTradeRepository;
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	PeriodTradeRepository periodTradeRepository;
	@Mock
	DistanceCalculator distanceCalculator;
	@InjectMocks
	SearchService searchService;

	List<DonationTrade> donationTrades = new ArrayList<>();
	List<ImmediateTrade> immediateTrades = new ArrayList<>();
	List<PeriodTrade> periodTrades = new ArrayList<>();
	Member member;
	SearchTradeReqDto reqDto;
	Double distance = 10.12;

	@BeforeEach
	void setUp() {
		member = Member.createBasicMember("test@test.com", "1234", "test", Address.builder().build());

		reqDto = SearchTradeReqDto.builder()
			.address1("상수동")
			.longitude(12.123)
			.latitude(40.123)
			.build();

		immediateTrades.add(
			ImmediateTrade.builder()
				.registeredProduct(RegisteredProduct.builder()
					.id(1L)
					.build())
				.title("Immediate banana")
				.latitude(12.1234)
				.longitude(40.546)
				.build());
	}

	@Test
	@DisplayName("검색 - 기존에 검색 이력이 없어서 생성하는 경우")
	void searchAndCreateKeywordAndFindTrades() {
		String word = "banana";
		SearchKeyword newKeyword = SearchKeyword.builder()
			.word(word)
			.build();

		when(searchKeywordRepository.findByWord(word)).thenReturn(Optional.empty());
		when(searchKeywordRepository.save(any(SearchKeyword.class))).thenReturn(newKeyword);
		when(immediateTradeRepository.findImmediateTradesWithProduct(word, reqDto.getAddress1())).thenReturn(
			immediateTrades);
		when(distanceCalculator.calculateDistance(reqDto.getLatitude(), reqDto.getLongitude(),
			immediateTrades.get(0).getLatitude(), immediateTrades.get(0).getLongitude())).thenReturn(distance);

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word, reqDto);

		assertThat(result.get(0).getTitle()).isEqualTo("Immediate banana");
		assertThat(result.get(0).getDistance()).isEqualTo(distance);

		verify(searchKeywordRepository, times(1)).findByWord(word);
		verify(searchKeywordRepository, times(2)).save(any(SearchKeyword.class));

	}

	@Test
	@DisplayName("검색 - 기존에 검색 이력이 있는 경우")
	void searchKeywordAndFindTrades() {
		String word = "banana";
		SearchKeyword existingKeyword = SearchKeyword.builder()
			.word(word)
			.build();

		when(searchKeywordRepository.findByWord(word)).thenReturn(Optional.of(existingKeyword));
		when(immediateTradeRepository.findImmediateTradesWithProduct(word, reqDto.getAddress1())).thenReturn(
			immediateTrades);
		when(distanceCalculator.calculateDistance(reqDto.getLatitude(), reqDto.getLongitude(),
			immediateTrades.get(0).getLatitude(), immediateTrades.get(0).getLongitude())).thenReturn(distance);

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word, reqDto);

		assertThat(result.get(0).getTitle()).isEqualTo("Immediate banana");

		verify(searchKeywordRepository, times(1)).findByWord(word);
		verify(searchKeywordRepository, times(1)).save(any(SearchKeyword.class));
	}

	@Test
	@DisplayName("검색 - 검색 결과가 없는 경우")
	void searchedNothing() {
		String word = "apple";
		SearchKeyword newKeyword = SearchKeyword.builder()
			.word(word)
			.build();

		when(searchKeywordRepository.findByWord(word)).thenReturn(Optional.empty());
		when(searchKeywordRepository.save(any(SearchKeyword.class))).thenReturn(newKeyword);

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word, reqDto);

		assertThat(result.get(0).getTitle()).isEqualTo("검색 결과가 없습니다.");

		verify(searchKeywordRepository, times(1)).findByWord(word);
		// save 를 2번 호출하게 됩니다. 생성에 1번, viewCount 를 업데이트 하는데 1번.
		verify(searchKeywordRepository, times(2)).save(any(SearchKeyword.class));
	}

	@Test
	@DisplayName("인기 top10")
	void findPopularKeywords() {
		List<SearchKeyword> searchKeywords = new ArrayList<>();

		searchKeywords.add(
			SearchKeyword.builder()
				.word("banana")
				.count(10L)
				.build()
		);

		searchKeywords.add(
			SearchKeyword.builder()
				.word("apple")
				.count(1L)
				.build()
		);

		when(searchKeywordRepository.findTop10ByOrderByCountDesc()).thenReturn(searchKeywords);

		List<String> result = searchService.findPopularKeywords();

		assertThat(result.get(0)).isEqualTo("banana");
		assertThat(result.get(1)).isEqualTo("apple");
	}

	@Test
	@DisplayName("인기 검색어를 찾을 수 없는 경우")
	void findPopularKeywordsButFoundNothing() {
		List<SearchKeyword> searchKeywords = new ArrayList<>();

		when(searchKeywordRepository.findTop10ByOrderByCountDesc()).thenReturn(searchKeywords);

		List<String> result = searchService.findPopularKeywords();

		assertThat(result.get(0)).isEqualTo("인기 검색어를 찾을 수 없습니다");
	}

	@Test
	@DisplayName("24시간이 지난 검색 기록 삭제")
	void deleteHistoryOver24hours() {
		ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

		searchService.deleteHistoryOver24hours();

		verify(searchHistoryRepository, times(1)).deleteBySearchedAtBefore(captor.capture());
		LocalDateTime capturedTime = captor.getValue();

		// 24시간 전인지 확인
		LocalDateTime expectedTime = LocalDateTime.now().minusHours(24);
		assertThat(capturedTime).isBeforeOrEqualTo(expectedTime);
		assertThat(capturedTime).isAfter(expectedTime.minusSeconds(5)); // 적절한 허용 오차
	}
}
