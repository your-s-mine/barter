package com.barter.domain.trade.immediatetrade.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateTest {
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

	VerifiedMember verifiedMember;

	@BeforeEach
	void setUp() {
		member = Member.createBasicMember("test@test.com", "1234", "test");

		verifiedMember = new VerifiedMember(member.getId(), member.getEmail());

		createRegisteredProductReqDto = new CreateRegisteredProductReqDto("등록 상품 제목", "등록 상품 설명");

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
	@DisplayName("즉시 교환 수정: 성공")
	void updateSuccess() throws IllegalAccessException {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(registeredProduct.getId(), "수정된 제목", "수정된 설명");

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.of(immediateTrade));
		when(registeredProductRepository.findById(registeredProduct.getId())).thenReturn(Optional.of(registeredProduct));
		when(immediateTradeRepository.save(any())).thenReturn(Optional.of(immediateTrade));

		// when
		FindImmediateTradeResDto result = immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto);

		// then
		assertThat(result.getTitle()).isEqualTo("수정된 제목");
		assertThat(result.getDescription()).isEqualTo("수정된 설명");
		verify(immediateTradeRepository).save(any());
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 권한 없음")
	void updateFailureNoAuthority() {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(registeredProduct.getId(), "수정된 제목", "수정된 설명");

		when(immediateTradeRepository.findById(1L)).thenReturn(Optional.of(immediateTrade));

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, 1L, new UpdateImmediateTradeReqDto(1L, "", "")))
			.isInstanceOf(IllegalAccessException.class);
	}
}
