package com.barter.domain.trade.immediatetrade;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

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
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class UpdateTest {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	RegisteredProductRepository registeredProductRepository;
	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	RegisteredProduct registeredProduct;
	ImmediateTrade immediateTrade;
	VerifiedMember verifiedMember;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.id(1L)
			.email("test@email.com")
			.password("1234")
			.build();

		verifiedMember = new VerifiedMember(member.getId(), member.getEmail());

		registeredProduct = RegisteredProduct.builder()
			.id(1L)
			.name("등록 상품 이름")
			.description("등록 상품 설명")
			.member(member)
			.build();

		immediateTrade = ImmediateTrade.builder()
			.id(1L)
			.title("즉시 교환 제목")
			.description("즉시 교환 설명")
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();
	}

	@Test
	@DisplayName("즉시 교환 수정: 성공")
	void updateSuccess() throws IllegalAccessException {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(registeredProduct.getId(), "수정된 제목",
			"수정된 설명");

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));
		when(registeredProductRepository.findById(registeredProduct.getId())).thenReturn(
			Optional.of(registeredProduct));
		when(immediateTradeRepository.save(any())).thenReturn(immediateTrade);

		// when
		FindImmediateTradeResDto result = immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto);

		// then
		assertThat(result.getTitle()).isEqualTo("수정된 제목");
		assertThat(result.getDescription()).isEqualTo("수정된 설명");
		verify(immediateTradeRepository).save(any());
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 교환을 찾지 못하는 경우")
	void updateFailureNotFoundTrade() {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(registeredProduct.getId(),
			registeredProduct.getName(), registeredProduct.getDescription());

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("해당 교환을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 수정 권한이 없는 경우")
	void updateFailureNoAuthority() {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(2L, "수정된 제목", "수정된 설명");

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		verifiedMember = new VerifiedMember(123L, member.getEmail());

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("해당 물품에 대한 권한이 없습니다.");
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 등록 물품을 찾을 수 없는 경우")
	void updateFailureNotFoundProduct() {
		// given
		UpdateImmediateTradeReqDto reqDto = new UpdateImmediateTradeReqDto(2L, "수정된 제목", "수정된 설명");

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("등록 물품을 찾을 수 없습니다.");
	}
}
