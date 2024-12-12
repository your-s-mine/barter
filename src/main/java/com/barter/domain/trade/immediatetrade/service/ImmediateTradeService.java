package com.barter.domain.trade.immediatetrade.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateStatusReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImmediateTradeService {
	private final ImmediateTradeRepository immediateTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final TradeProductRepository tradeProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;

	public FindImmediateTradeResDto create(CreateImmediateTradeReqDto reqDto) {
		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProduct().getId()).orElseThrow(
				() -> new IllegalArgumentException("등록 물품을 찾을 수 없습니다.")
			);

		ImmediateTrade immediateTrade = ImmediateTrade.builder()
			.title(reqDto.getTitle())
			.description(reqDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();

		ImmediateTrade savedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(savedTrade);
	}

	@Transactional
	public FindImmediateTradeResDto find(Long tradeId) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.addViewCount();

		return FindImmediateTradeResDto.from(immediateTrade);
	}

	@Transactional(readOnly = true)
	public PagedModel<FindImmediateTradeResDto> findImmediateTrades(Pageable pageable) {
		Page<FindImmediateTradeResDto> trades = immediateTradeRepository.findAll(pageable)
			.map(trade -> FindImmediateTradeResDto.from(trade));

		return new PagedModel<>(trades);
	}


	public FindImmediateTradeResDto update(VerifiedMember member, Long tradeId, UpdateImmediateTradeReqDto reqDto) throws
		IllegalAccessException {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProductId()).orElseThrow(
				() -> new IllegalArgumentException("등록 물품을 찾을 수 없습니다.")
			);

		if (registeredProduct.getMember().getId() == immediateTrade.getProduct().getMember().getId()) {
			throw new IllegalAccessException("등록한 사람만이 수정할 수 있습니다");
		}

		immediateTrade.update(reqDto);

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(updatedTrade);
	}

	public String delete(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		immediateTradeRepository.delete(immediateTrade);

		return "교환 삭제 완료";
	}

	@Transactional
	public String createTradeSuggest(Long tradeId, CreateTradeSuggestProductReqDto reqDto, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateIsSelfSuggest(member.getId());

		if (!immediateTrade.validateTradeStatus(immediateTrade.getStatus())) {
			throw new IllegalStateException("해당 교환에 제안할 수 없습니다.");
		}

		List<TradeProduct> tradeProducts = new ArrayList<>();

		for (Long productId : reqDto.getSuggestedProductIds()) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(productId).orElseThrow(
				() -> new IllegalArgumentException("제안 상품을 찾을 수 없습니다.")
			);

			if (!suggestedProduct.validateProductStatus(suggestedProduct.getStatus())) {
				throw new IllegalArgumentException("해당 상품으로 제안하실 수 없습니다.");
			}

			suggestedProduct.changStatusSuggesting();

			TradeProduct tradeProduct = TradeProduct.builder()
				.suggestedProduct(suggestedProduct)
				.tradeType(TradeType.IMMEDIATE)
				.build();

			tradeProducts.add(tradeProduct);
		}

		tradeProductRepository.saveAll(tradeProducts);
		return "제안 완료";
	}

	@Transactional
	public String acceptTradeSuggest(Long tradeId) {
		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정 - 교환을 생성한 맴버만이 승낙할 수 있음

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.changeStatusInProgress();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changStatusAccepted();
		}

		return "제안 승락 완료";
	}

	// 제안 거절 시 `교환_제안_물품` 테이블에서 삭제. 기준 "tradeId - 교환 Id"

	@Transactional
	public String denyTradeSuggest(Long tradeId) {

		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정 - 교환을 생성한 맴버만이 거절할 수 있음

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.changeStatusPending();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changStatusPending();
		}

		tradeProductRepository.deleteAll(tradeProducts);

		return "제안 거절";
	}

	@Transactional
	public FindImmediateTradeResDto updateStatus(Long tradeId, UpdateStatusReqDto reqDto) {

		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		if (!(immediateTrade.isCompleted())) {
			throw new IllegalStateException("교환이 완료된 건에 대해서는 상태 변경을 할 수 없습니다.");
		}

		immediateTrade.changeStatus(reqDto.getTradeStatus());

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(updatedTrade);
	}
}