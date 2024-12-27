package com.barter.domain.trade.immediatetrade;

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

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.response.FindSuggestForImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;
import com.barter.exception.customexceptions.ProductException;

@ExtendWith(MockitoExtension.class)
public class FindSuggestTest {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	TradeProductRepository tradeProductRepository;
	@Mock
	SuggestedProductRepository suggestedProductRepository;
	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	RegisteredProduct registeredProduct;
	ImmediateTrade immediateTrade;
	VerifiedMember verifiedMember;
	Member suggester;
	Member suggester2;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.id(1L)
			.email("member")
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

		suggester = Member.builder()
			.id(111L)
			.email("suggester")
			.password("1234")
			.build();

		suggester2 = Member.builder()
			.id(222L)
			.email("suggester")
			.password("1234")
			.build();

	}

	@Test
	@DisplayName("성공")
	void success() {
		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		SuggestedProduct suggestedProduct1 = SuggestedProduct.builder()
			.id(1L)
			.name("제안 수락됨")
			.status(SuggestedStatus.ACCEPTED)
			.member(suggester)
			.build();

		SuggestedProduct suggestedProduct2 = SuggestedProduct.builder()
			.id(2L)
			.name("제안 중")
			.status(SuggestedStatus.SUGGESTING)
			.member(suggester2)
			.build();

		List<TradeProduct> tradeProducts = new ArrayList<>();

		tradeProducts.add(TradeProduct.builder()
			.suggestedProduct(suggestedProduct1)
			.build());

		tradeProducts.add(TradeProduct.builder()
			.suggestedProduct(suggestedProduct2)
			.build());

		when(tradeProductRepository.findAllByTradeIdAndTradeType(
			immediateTrade.getId(), TradeType.IMMEDIATE)).thenReturn(tradeProducts);

		when(suggestedProductRepository.findById(1L)).thenReturn(Optional.of(suggestedProduct1));
		when(suggestedProductRepository.findById(2L)).thenReturn(Optional.of(suggestedProduct2));

		List<FindSuggestForImmediateTradeResDto> result =
			immediateTradeService.findSuggestForImmediateTrade(immediateTrade.getId(), verifiedMember);

		assertThat(result.get(0).getSuggestedProductName()).isEqualTo("제안 수락됨");
		assertThat(result.get(0).getSuggestedProductStatus()).isEqualTo(SuggestedStatus.ACCEPTED);
		assertThat(result.get(0).getMemberId()).isEqualTo(111L);

		assertThat(result.get(1).getSuggestedProductName()).isEqualTo("제안 중");
		assertThat(result.get(1).getSuggestedProductStatus()).isEqualTo(SuggestedStatus.SUGGESTING);
		assertThat(result.get(1).getMemberId()).isEqualTo(222L);

		verify(suggestedProductRepository, times(2)).findById(anyLong());
	}

	@Test
	@DisplayName("실패: 제안 물품을 찾을 수 없는 경우")
	void failure() {
		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		List<TradeProduct> tradeProducts = new ArrayList<>();
		tradeProducts.add(TradeProduct.builder()
			.suggestedProduct(SuggestedProduct.builder().id(1L).build())
			.build());

		when(tradeProductRepository.findAllByTradeIdAndTradeType(
			immediateTrade.getId(), TradeType.IMMEDIATE)).thenReturn(tradeProducts);

		when(suggestedProductRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() ->
			immediateTradeService.findSuggestForImmediateTrade(immediateTrade.getId(), verifiedMember))
			.isInstanceOf(ProductException.class).hasMessage("존재하지 않는 제안물품입니다.");
	}
}
