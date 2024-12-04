package com.barter.domain.trade.immediatetrade.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImmediateTradeService {
	private final ImmediateTradeRepository immediateTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;

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

}
