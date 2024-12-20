package com.barter.domain.search;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.entity.SearchHistory;
import com.barter.domain.search.entity.SearchKeyword;
import com.barter.domain.search.repository.SearchHistoryRepository;
import com.barter.domain.search.repository.SearchKeywordRepository;
import com.barter.domain.search.service.SearchService;
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
	@InjectMocks
	SearchService searchService;

	List<DonationTrade> donationTrades = new ArrayList<>();
	List<ImmediateTrade> immediateTrades = new ArrayList<>();
	List<PeriodTrade> periodTrades = new ArrayList<>();

	@Test
	@DisplayName("검색 - 기존에 검색 이력이 없어서 생성하는 경우")
	void searchAndCreateKeywordAndFindTrades() {
		String word = "banana";
		SearchKeyword newKeyword = SearchKeyword.builder()
			.word(word)
			.build();

		immediateTrades.add(
			ImmediateTrade.builder()
				.title("Immediate banana")
				.build());

		when(searchKeywordRepository.findByWord(word)).thenReturn(Optional.empty());
		when(searchKeywordRepository.save(any(SearchKeyword.class))).thenReturn(newKeyword);

		when(immediateTradeRepository.findByTitleOrDescriptionContaining(word, word)).thenReturn(immediateTrades);

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word);

		assertThat(result.get(0).getTitle()).isEqualTo("Immediate banana");

		verify(searchKeywordRepository, times(1)).findByWord(word);
		// save 를 2번 호출하게 됩니다. 생성에 1번, viewCount 를 업데이트 하는데 1번.
		verify(searchKeywordRepository, times(2)).save(any(SearchKeyword.class));

	}

	@Test
	@DisplayName("검색 - 기존에 검색 이력이 있는 경우")
	void searchKeywordAndFindTrades() {
		String word = "banana";
		SearchKeyword existingKeyword = SearchKeyword.builder()
			.word(word)
			.build();

		donationTrades.add(
			DonationTrade.builder()
				.title("Donation banana")
				.build());

		periodTrades.add(
			PeriodTrade.builder()
				.title("PeriodTrade banana")
				.build());

		when(searchKeywordRepository.findByWord(word)).thenReturn(Optional.of(existingKeyword));

		// when(donationTradeRepository.findByTitleOrDescriptionContaining(word, word)).thenReturn(donationTrades)
		when(periodTradeRepository.findByTitleOrDescriptionContaining(word, word)).thenReturn(periodTrades);

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word);

		assertThat(result.get(0).getTitle()).isEqualTo("Donation banana");
		assertThat(result.get(1).getTitle()).isEqualTo("PeriodTrade banana");

		verify(searchKeywordRepository, times(1)).findByWord(word);
		verify(searchKeywordRepository, times(1)).save(any(SearchKeyword.class));
	}

	@Test
	@DisplayName("검색 - 공란으로 검색한 경우")
	void searchBlank() {
		String word = " ";

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word);

		assertThat(result.get(0).getTitle()).isEqualTo("검색어를 입력해주세요.");
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

		List<SearchTradeResDto> result = searchService.searchKeywordAndFindTrades(word);

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
		SearchKeyword banana = SearchKeyword.builder()
			.word("banana")
			.build();
		SearchKeyword apple = SearchKeyword.builder()
			.word("apple")
			.build();

		List<SearchHistory> oldHistories = List.of(
			SearchHistory.builder()
				.searchKeyword(banana)
				.build(),
			SearchHistory.builder()
				.searchKeyword(apple)
				.build()
		);

		when(searchHistoryRepository.findAllBySearchedAt(any())).thenReturn(oldHistories);

		searchService.deleteHistoryOver24hours();

		verify(searchHistoryRepository, times(1)).findAllBySearchedAt(any());
		verify(searchHistoryRepository, times(1)).deleteAll(oldHistories);
	}
}
