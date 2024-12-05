package com.barter.domain.trade.immediatetrade.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
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

	// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정

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

	public FindImmediateTradeResDto update(Long tradeId, UpdateImmediateTradeReqDto reqDto) throws
		IllegalAccessException {

		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

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

	public String delete(Long tradeId) {

		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTradeRepository.delete(immediateTrade);

		return "교환 삭제 완료";
	}

	// 즉시 교환 제안 생성 - in-progress, completed 상태에선 불가능
	public String createSuggest(Long tradeId, CreateTradeSuggestProductReqDto reqDto) {
		// todo: 유저 정보를 받아와 권한 확인 로직 추가 및 수정 ex - 본인이 등록한 교환에 제안 불가

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		if (!validateTradeStatus(immediateTrade.getStatus())) { // PENDING 상태인 거래에만 제안 가능
			throw new IllegalStateException("해당 교환에 제안할 수 없습니다.");
		}

		for (SuggestedProduct product : reqDto.getSuggestedProductList()) {
			if (!validateProductStatus(product.getStatus())) { // PENDING 상태인 물품으로만 제안 가능
				throw new IllegalArgumentException("해당 상품으로 제안하실 수 없습니다.");
			}

			TradeProduct tradeProduct = TradeProduct.builder()
				.suggestedProduct(product)
				.tradeType(TradeType.IMMEDIATE)
				.build();

			tradeProductRepository.save(tradeProduct);
		}

		return "제안 완료";
	}

	// 제안 거절 시 `교환_제안_물품` 테이블에서 삭제. 기준 "tradeId - 교환 Id"

	private boolean validateTradeStatus(TradeStatus status) {
		return status.equals(TradeStatus.PENDING);
	}

	private boolean validateProductStatus(SuggestedStatus status) {
		return status.equals(SuggestedStatus.PENDING);
	}
}
