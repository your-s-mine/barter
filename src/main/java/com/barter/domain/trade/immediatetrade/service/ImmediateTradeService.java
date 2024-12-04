package com.barter.domain.trade.immediatetrade.service;

import org.springframework.stereotype.Service;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeRequestDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImmediateTradeService {
	// 추가 의존성: 등록 물품 레포지토리

	private final ImmediateTradeRepository immediateTradeRepository;

	public String create(CreateImmediateTradeRequestDto requestDto) {
		// todo: 등록 물품 레포지토리에서 물품이 있는지 확인 findById() `registeredProduct.getId()`
		RegisteredProduct registeredProduct = requestDto.getRegisteredProduct();

		ImmediateTrade immediateTrade = ImmediateTrade.builder()
			.title(requestDto.getTitle())
			.description(requestDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();

		immediateTradeRepository.save(immediateTrade);
		return "교환 물품 등록 완료";
	}
}
