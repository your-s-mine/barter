package com.barter.domain.trade.immediatetrade.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.service.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.service.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.service.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class find {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	RegisteredProductRepository registeredProductRepository;
	@Mock
	TradeProductRepository tradeProductRepository;
	@Mock
	SuggestedProductRepository suggestedProductRepository;
	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	CreateRegisteredProductReqDto createRegisteredProductReqDto;
	RegisteredProduct registeredProduct;
	CreateImmediateTradeReqDto createImmediateTradeReqDto;
	ImmediateTrade immediateTrade;

	@BeforeEach
	void setUp() {
		member = Member.createBasicMember("test@test.com", "1234", "test");
		createRegisteredProductReqDto = new CreateRegisteredProductReqDto(
			"등록 상품 제목", "등록 상품 설명");

		List<String> images = new ArrayList<>();
		images.add("testImage");

		registeredProduct = RegisteredProduct.create(createRegisteredProductReqDto, member, images);

		createImmediateTradeReqDto = new CreateImmediateTradeReqDto(registeredProduct, "즉시 교환 제목", "즉시 교환 설명");

		immediateTrade = ImmediateTrade.builder()
			.title(createImmediateTradeReqDto.getTitle())
			.description(createImmediateTradeReqDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();
	}




}
