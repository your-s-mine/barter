package com.barter.domain.trade.immediatetrade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateStatusReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class TradeSuggestTest {

	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	SuggestedProductRepository suggestedProductRepository;
	@Mock
	TradeProductRepository tradeProductRepository;
	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	RegisteredProduct registeredProduct;
	ImmediateTrade immediateTrade;
	VerifiedMember verifiedMember;
	CreateTradeSuggestProductReqDto reqDto;
	SuggestedProduct suggestedProduct;
	SuggestedProduct suggestedProduct2;
	VerifiedMember suggester;
	UpdateStatusReqDto updateStatusReqDto;

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

		reqDto = new CreateTradeSuggestProductReqDto();
		reqDto.getSuggestedProductIds().addAll(Arrays.asList(1L, 2L));

		suggestedProduct = SuggestedProduct.builder()
			.id(1L)
			.status(SuggestedStatus.PENDING)
			.build();

		suggestedProduct2 = SuggestedProduct.builder()
			.id(2L)
			.status(SuggestedStatus.PENDING)
			.build();

		suggester = new VerifiedMember(2L, "suggester");
	}

	@Test
	@DisplayName("즉시 교환 - 제안 생성: 성공")
	void createSuggestSuccess() {

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));
		when(suggestedProductRepository.findById(1L)).thenReturn(Optional.of(suggestedProduct));
		when(suggestedProductRepository.findById(2L)).thenReturn(Optional.of(suggestedProduct2));

		String result = immediateTradeService.createTradeSuggest(immediateTrade.getId(), reqDto, suggester);

		assertThat(result).isEqualTo("제안 완료");

		assertThat(suggestedProduct.getStatus()).isEqualTo(SuggestedStatus.SUGGESTING);
		assertThat(suggestedProduct2.getStatus()).isEqualTo(SuggestedStatus.SUGGESTING);

		verify(tradeProductRepository, times(1)).saveAll(any());

	}

	@Test
	@DisplayName("즉시 교환 - 제안 수락: 성공")
	void acceptSuggestSuccess() {

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		List<TradeProduct> tradeProducts = new ArrayList<>();
		tradeProducts.add(TradeProduct.builder()
			.tradeId(immediateTrade.getId())
			.tradeType(TradeType.IMMEDIATE)
			.suggestedProduct(suggestedProduct)
			.build());

		tradeProducts.add(TradeProduct.builder()
			.tradeId(immediateTrade.getId())
			.tradeType(TradeType.IMMEDIATE)
			.suggestedProduct(suggestedProduct2)
			.build());

		when(tradeProductRepository.findAllByTradeId(immediateTrade.getId())).thenReturn(tradeProducts);

		String result = immediateTradeService.acceptTradeSuggest(immediateTrade.getId(), verifiedMember);

		assertThat(result).isEqualTo("제안 수락 완료");

		assertThat(immediateTrade.getStatus()).isEqualTo(TradeStatus.IN_PROGRESS);
		assertThat(suggestedProduct.getStatus()).isEqualTo(SuggestedStatus.ACCEPTED);
		assertThat(suggestedProduct2.getStatus()).isEqualTo(SuggestedStatus.ACCEPTED);

	}
}
