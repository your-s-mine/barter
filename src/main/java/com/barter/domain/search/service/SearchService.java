package com.barter.domain.search.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.search.dto.ConvertRegisteredProductDto;
import com.barter.domain.search.dto.SearchTradeReqDto;
import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.entity.SearchHistory;
import com.barter.domain.search.entity.SearchKeyword;
import com.barter.domain.search.repository.SearchHistoryRepository;
import com.barter.domain.search.repository.SearchKeywordRepository;
import com.barter.domain.search.util.DistanceCalculator;
import com.barter.domain.trade.TradeCommonEntity;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

	private final SearchKeywordRepository searchKeywordRepository;
	private final SearchHistoryRepository searchHistoryRepository;
	private final DonationTradeRepository donationTradeRepository;
	private final ImmediateTradeRepository immediateTradeRepository;
	private final PeriodTradeRepository periodTradeRepository;
	private final MemberRepository memberRepository;
	private final DistanceCalculator distanceCalculator;

	@Transactional
	public List<SearchTradeResDto> searchKeywordAndFindTrades(String word, SearchTradeReqDto reqDto) {

		SearchKeyword searchKeyword = searchKeywordRepository.findByWord(word)
			.orElseGet(() -> searchKeywordRepository.save(
				SearchKeyword.builder()
					.word(word)
					.count(0L)
					.build()
			));

		searchHistoryRepository.save(SearchHistory.builder()
			.searchKeyword(searchKeyword)
			.build()
		);

		asyncUpdateSearchKeywordCount(searchKeyword.getId(), searchKeyword);

		// todo: 기부 교환에 '위치' 추가되면, 검색에 기부 교환 추가

		List<ImmediateTrade> immediateTrades = immediateTradeRepository.findImmediateTradesWithProduct(word,
			reqDto.getAddress1());

		List<PeriodTrade> periodTrades = periodTradeRepository.findPeriodTradesWithProduct(word,
			reqDto.getAddress1());

		List<SearchTradeResDto> tradeDtos = new ArrayList<>();
		tradeDtos.addAll(mapTradesToSearchTradeRes(immediateTrades, reqDto.getLongitude(), reqDto.getLatitude()));
		tradeDtos.addAll(mapTradesToSearchTradeRes(periodTrades, reqDto.getLongitude(), reqDto.getLatitude()));

		if (tradeDtos.isEmpty()) {
			tradeDtos.add(SearchTradeResDto.builder()
				.title("검색 결과가 없습니다.")
				.build());
		}

		return tradeDtos;
	}

	public List<String> findPopularKeywords() {
		List<SearchKeyword> searchKeywords = searchKeywordRepository.findTop10ByOrderByCountDesc();

		if (searchKeywords.isEmpty()) {
			searchKeywords.add(SearchKeyword.builder()
				.word("인기 검색어를 찾을 수 없습니다")
				.build());
		}

		return searchKeywords.stream().map(topKeyword -> topKeyword.getWord()).toList();
	}

	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 5000)
	)
	@Scheduled(cron = "0 */3 * * * *")
	@Transactional
	public void deleteHistoryOver24hours() {
		LocalDateTime time = LocalDateTime.now().minusHours(24);

		searchHistoryRepository.deleteBySearchedAtBefore(time);
	}

	@Recover
	public void recover(Exception e) {
		log.error("검색 기록 데이터 삭제 오류");
	}

	@Async
	public void asyncUpdateSearchKeywordCount(Long searchKeywordId, SearchKeyword searchKeyword) {
		LocalDateTime since = LocalDateTime.now().minusHours(24);
		Long recentCount = searchHistoryRepository.countRecentSearches(searchKeywordId, since);

		searchKeyword.updateCount(recentCount);
		searchKeywordRepository.save(searchKeyword);
	}

	private <T extends TradeCommonEntity> List<SearchTradeResDto> mapTradesToSearchTradeRes(List<T> trades,
		Double memberLongitude, Double memberLatitude) {

		List<SearchTradeResDto> resDtos = new ArrayList<>();
		for (T trade : trades) {
			Double latitude = trade.getLatitude();
			Double longitude = trade.getLongitude();

			Double distance = distanceCalculator.calculateDistance(memberLatitude, memberLongitude, latitude,
				longitude);

			resDtos.add(SearchTradeResDto.builder()
				.title(trade.getTitle())
				.product(createConvertedProductDto(trade.getRegisteredProduct()))
				.tradeStatus(trade.getStatus())
				.viewCount(trade.getViewCount())
				.distance(distance)
				.build()
			);
		}

		return resDtos;
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
