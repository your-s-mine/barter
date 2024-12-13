package com.barter.domain.search.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.entity.SearchHistory;
import com.barter.domain.search.entity.SearchKeyword;
import com.barter.domain.search.repository.SearchHistoryRepository;
import com.barter.domain.search.repository.SearchKeywordRepository;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.immediatetrade.service.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.service.repository.ImmediateTradeRepository;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final SearchKeywordRepository searchKeywordRepository;
	private final SearchHistoryRepository searchHistoryRepository;
	private final DonationTradeRepository donationTradeRepository;
	private final ImmediateTradeRepository immediateTradeRepository;
	private final PeriodTradeRepository periodTradeRepository;

	@Transactional
	public List<SearchTradeResDto> createSearchKeywordAndFindTrades(String word) {

		// 검색어 찾고 없다면 생성
		SearchKeyword searchKeyword = searchKeywordRepository.findByWord(word)
			.orElseGet(() ->
				searchKeywordRepository.save(SearchKeyword.builder()
					.word(word)
					.build()
				));

		// 검색 이력에 저장
		searchHistoryRepository.save(SearchHistory.builder()
			.searchKeyword(searchKeyword)
			.build()
		);

		// 24시간 내 검색 횟수 계산
		LocalDateTime since = LocalDateTime.now().minusHours(24);
		Long recentCount = searchHistoryRepository.countRecentSearches(searchKeyword.getId(), since);

		// 검색 count 업데이트
		searchKeyword.updateCount(recentCount);
		searchKeywordRepository.save(searchKeyword);

		// 검색어로 교환 찾기
		List<DonationTrade> donationTrades = donationTradeRepository.findByTitleOrDescriptionContaining(word, word);
		List<ImmediateTrade> immediateTrades = immediateTradeRepository.findByTitleOrDescriptionContaining(word, word);
		List<PeriodTrade> periodTrades = periodTradeRepository.findByTitleOrDescriptionContaining(word, word);

		// 찾은 교환 List 를 변환하여 추가
		List<SearchTradeResDto> tradeDtos = new ArrayList<>();
		tradeDtos.addAll(mapDonationTradesToSearchTradeRes(donationTrades));
		tradeDtos.addAll(mapImmediateTradesToSearchTradeRes(immediateTrades));
		tradeDtos.addAll(mapPeriodTradesToSearchTradeRes(periodTrades));

		if (tradeDtos.isEmpty()) {
			tradeDtos.add(SearchTradeResDto.builder()
				.title("검색 결과가 없습니다.")
				.build());
		}

		return tradeDtos;
	}

	public List<String> findPopularKeywords() {

		List<SearchKeyword> searchKeywords = searchKeywordRepository.findTop10ByOrderByCountDesc();

		return searchKeywords.stream().map(topKeyword -> topKeyword.getWord()).toList();
	}

	@Scheduled(cron = "0 */30 * * * *")
	@Transactional
	public void deleteHistoryOver24hours() {
		LocalDateTime time = LocalDateTime.now().minusHours(24);

		List<SearchHistory> searchHistories = searchHistoryRepository.findAllBySearchedAt(time);

		searchHistoryRepository.deleteAll(searchHistories);
	}

	private List<SearchTradeResDto> mapDonationTradesToSearchTradeRes(List<DonationTrade> trades) {
		return trades.stream()
			.map(trade -> SearchTradeResDto.builder()
				.title(trade.getTitle())
				.product(trade.getProduct())
				.tradeStatus(trade.getStatus())
				.viewCount(trade.getViewCount())
				.build())
			.toList();
	}

	private List<SearchTradeResDto> mapImmediateTradesToSearchTradeRes(List<ImmediateTrade> trades) {
		return trades.stream()
			.map(trade -> SearchTradeResDto.builder()
				.title(trade.getTitle())
				.product(trade.getProduct())
				.tradeStatus(trade.getStatus())
				.viewCount(trade.getViewCount())
				.build())
			.toList();
	}

	private List<SearchTradeResDto> mapPeriodTradesToSearchTradeRes(List<PeriodTrade> trades) {
		return trades.stream()
			.map(trade -> SearchTradeResDto.builder()
				.title(trade.getTitle())
				.product(trade.getRegisteredProduct())
				.tradeStatus(trade.getStatus())
				.viewCount(trade.getViewCount())
				.build())
			.toList();
	}
}
