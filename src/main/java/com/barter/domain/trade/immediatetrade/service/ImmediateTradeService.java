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

	@Transactional
	public FindImmediateTradeResDto update(VerifiedMember member, Long tradeId,
		UpdateImmediateTradeReqDto reqDto) throws
		IllegalAccessException {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProductId()).orElseThrow(
				() -> new IllegalArgumentException("등록 물품을 찾을 수 없습니다.")
			);

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
			throw new IllegalStateException("PENDING 상태의 교환에만 제안할 수 있습니다.");
		}

		List<TradeProduct> tradeProducts = new ArrayList<>();

		for (Long productId : reqDto.getSuggestedProductIds()) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(productId).orElseThrow(
				() -> new IllegalArgumentException("제안 상품을 찾을 수 없습니다.")
			);

			if (!suggestedProduct.validateProductStatus(suggestedProduct.getStatus())) {
				throw new IllegalArgumentException("PENDING 상태의 상품으로만 제안하실 수 있습니다.");
			}

			suggestedProduct.changStatusSuggesting();

			TradeProduct tradeProduct = TradeProduct.builder()
				.tradeId(tradeId)
				.suggestedProduct(suggestedProduct)
				.tradeType(TradeType.IMMEDIATE)
				.build();

			tradeProducts.add(tradeProduct);
		}

		tradeProductRepository.saveAll(tradeProducts);
		return "제안 완료";
	}

	@Transactional
	public String acceptTradeSuggest(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		immediateTrade.changeStatusInProgress();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changStatusAccepted();
		}

		return "제안 수락 완료";
	}

	@Transactional
	public String denyTradeSuggest(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());
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
	public FindImmediateTradeResDto updateStatus(Long tradeId, UpdateStatusReqDto reqDto, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		if ((immediateTrade.isCompleted())) {
			throw new IllegalStateException("교환이 완료된 건에 대해서는 상태 변경을 할 수 없습니다.");
		}

		immediateTrade.changeStatus(reqDto.getTradeStatus());

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(updatedTrade);
	}
}