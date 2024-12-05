package com.barter.domain.trade.periodtrade.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.SuggestedPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.SuggestedPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.UpdatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.UpdatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodTradeService {

	private final PeriodTradeRepository periodTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final MemberRepository memberRepository;
	private final TradeProductRepository tradeProductRepository;

	public CreatePeriodTradeResDto createPeriodTrades(CreatePeriodTradeReqDto reqDto) {

		/* TODO : RegisteredProduct 가 해당 유저의 물건인지 확인하는 로직 필요
		    해당 로직 추가시 아래 코드는 변경 될 수 있음*/

		PeriodTrade periodTrade = PeriodTrade.createInitPeriodTrade(reqDto.getTitle(), reqDto.getDescription(),
			reqDto.getProduct(),
			reqDto.getEndedAt());

		periodTrade.validateIsExceededMaxEndDate();

		return CreatePeriodTradeResDto.from(periodTradeRepository.save(periodTrade));
	}

	// TODO : (PeriodTrade 조회) : 멤버 구현 시 멤버정보를 받아서 해당 멤버가 참여한 PeriodTrade 만 조회 가능하도록 하기

	@Transactional(readOnly = true)
	public PagedModel<FindPeriodTradeResDto> findPeriodTrades(Pageable pageable) {
		Page<FindPeriodTradeResDto> trades = periodTradeRepository.findAll(pageable)
			.map(FindPeriodTradeResDto::from);
		return new PagedModel<>(trades);
	}

	@Transactional(readOnly = true)
	public FindPeriodTradeResDto findPeriodTradeById(Long id) {
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.addViewCount();

		return FindPeriodTradeResDto.from(periodTrade);
	}

	@Transactional
	public UpdatePeriodTradeResDto updatePeriodTrade(Long id, UpdatePeriodTradeReqDto reqDto) {

		Long userId = 1L;
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(userId);
		periodTrade.validateIsCompleted();
		periodTrade.update(reqDto.getTitle(), reqDto.getDescription());

		return UpdatePeriodTradeResDto.from(periodTrade); // save 써도 되고 안써도 되고

	}

	@Transactional
	public void deletePeriodTrade(Long id) {
		Long userId = 1L;
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.validateAuthority(userId);
		periodTrade.validateIsCompleted();
		periodTradeRepository.delete(periodTrade);

	}

	@Transactional
	public SuggestedPeriodTradeResDto suggestPeriodTrade(Long id, SuggestedPeriodTradeReqDto reqDto) {

		Long userId = 2L; // 게시글에 제안하는 멤버
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.validateSuggestAuthority(userId); // 자신의 교환 (게시글) 에 제안 불가

		// 기간 교환 시작 전(PENDING) 또는 이미 거래된(COMPLETED) 교환인 경우
		periodTrade.validateIsPending();
		periodTrade.validateIsCompleted();

		List<RegisteredProduct> registeredProduct = findRegisteredProductByIds(reqDto.getProductIds());

		registeredProduct.forEach(product -> {
			TradeProduct tradeProduct = TradeProduct.createTradeProduct(
				id, TradeType.PERIOD, product
			);

			tradeProductRepository.save(tradeProduct);
		});

		return SuggestedPeriodTradeResDto.from(id, registeredProduct);

	}

	private List<RegisteredProduct> findRegisteredProductByIds(List<Long> productIds) {
		return productIds.stream()
			.map(id -> registeredProductRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 id 의 등록된 제품이 없습니다.")))
			.toList();
	}
}
