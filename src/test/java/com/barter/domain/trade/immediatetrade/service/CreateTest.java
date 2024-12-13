package com.barter.domain.trade.immediatetrade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;

@ExtendWith(MockitoExtension.class)
public class CreateTest {

	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	RegisteredProductRepository registeredProductRepository;
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

	@Test
	@DisplayName("즉시 교환 생성: 성공")
	void success() {
		// given
		when(registeredProductRepository.findById(createImmediateTradeReqDto.getRegisteredProduct().getId()))
			.thenReturn(Optional.ofNullable(registeredProduct));

		when(immediateTradeRepository.save(any())).thenReturn(immediateTrade);



		// when
		FindImmediateTradeResDto resDto = immediateTradeService.create(createImmediateTradeReqDto);

		// then
		assertThat(resDto.getTitle()).isEqualTo("즉시 교환 제목");
		assertThat(resDto.getDescription()).isEqualTo("즉시 교환 설명");
		assertThat(resDto.getProductId()).isEqualTo(registeredProduct.getId());
	}

	@Test
	@DisplayName("즉시 교환 생성: 실패 - 등록 물품을 찾을 수 없는 경우")
	void failure() {
		// given
		when(registeredProductRepository.findById(createImmediateTradeReqDto.getRegisteredProduct().getId()))
			.thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> immediateTradeService.create(createImmediateTradeReqDto))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
