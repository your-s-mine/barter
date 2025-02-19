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
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.customexceptions.ImmediateTradeException;
import com.barter.exception.customexceptions.ProductException;

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
			.registeredProduct(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();
	}

	@Test
	@DisplayName("즉시 교환 수정: 성공")
	void updateSuccess() throws IllegalAccessException {
		// given
		UpdateImmediateTradeReqDto reqDto = UpdateImmediateTradeReqDto.builder()
			.registeredProductId(registeredProduct.getId())
			.title("제목 수정")
			.description("설명 수정")
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));
		when(registeredProductRepository.findById(registeredProduct.getId())).thenReturn(
			Optional.of(registeredProduct));
		when(immediateTradeRepository.save(any())).thenReturn(immediateTrade);

		// when
		FindImmediateTradeResDto result = immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto);

		// then
		assertThat(result.getTitle()).isEqualTo("제목 수정");
		assertThat(result.getDescription()).isEqualTo("설명 수정");
		verify(immediateTradeRepository).save(any());
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 교환을 찾지 못하는 경우")
	void updateFailureNotFoundTrade() {
		// given
		UpdateImmediateTradeReqDto reqDto = UpdateImmediateTradeReqDto.builder()
			.registeredProductId(registeredProduct.getId())
			.title("제목 수정")
			.description("설명 수정")
			.build();

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(ImmediateTradeException.class).hasMessage("존재하지 않는 즉시 교환입니다.");
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 수정 권한이 없는 경우")
	void updateFailureNoAuthority() {
		// given
		UpdateImmediateTradeReqDto reqDto = UpdateImmediateTradeReqDto.builder()
			.registeredProductId(2L)
			.title("제목 수정")
			.description("설명 수정")
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		verifiedMember = new VerifiedMember(123L, member.getEmail());

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(AuthException.class).hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("즉시 교환 수정: 실패 - 등록 물품을 찾을 수 없는 경우")
	void updateFailureNotFoundProduct() {
		// given
		UpdateImmediateTradeReqDto reqDto = UpdateImmediateTradeReqDto.builder()
			.registeredProductId(registeredProduct.getId())
			.title("제목 수정")
			.description("설명 수정")
			.build();
		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		// when, then
		assertThatThrownBy(() ->
			immediateTradeService.update(verifiedMember, immediateTrade.getId(), reqDto))
			.isInstanceOf(ProductException.class).hasMessage("존재하지 않는 등록물품입니다.");
	}
}
