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
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class DeleteTest {
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
	@DisplayName("즉시 교환: 삭제 성공")
	void deleteSuccess() {
		// given
		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		// when
		String result = immediateTradeService.delete(immediateTrade.getId(), verifiedMember);

		// then
		assertThat(result).isEqualTo("교환 삭제 완료");
		verify(immediateTradeRepository).findById(immediateTrade.getId());
		verify(immediateTradeRepository).delete(immediateTrade);
	}

	@Test
	@DisplayName("즉시 교환: 삭제 실패 - 교환을 찾을 수 없는 경우")
	void deleteFailureNotFoundTrade() {

		assertThatThrownBy(() ->
			immediateTradeService.delete(123L, verifiedMember))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("해당 교환을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("즉시 교환: 삭제 실패 - 권한이 없는 경우")
	void deleteFailureNoAuthority() {
		// given
		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		verifiedMember = new VerifiedMember(123L, member.getEmail());

		assertThatThrownBy(() ->
			immediateTradeService.delete(immediateTrade.getId(), verifiedMember))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("해당 물품에 대한 권한이 없습니다.");
	}
}
