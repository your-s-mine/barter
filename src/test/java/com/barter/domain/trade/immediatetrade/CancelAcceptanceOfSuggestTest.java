package com.barter.domain.trade.immediatetrade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;
import com.barter.exception.customexceptions.ImmediateTradeException;

@ExtendWith(MockitoExtension.class)
public class CancelAcceptanceOfSuggestTest {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	TradeProductRepository tradeProductRepository;
	@Mock
	NotificationService notificationService;
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

	}

	@Test
	@DisplayName("성공")
	void Success() {
		immediateTrade = ImmediateTrade.builder()
			.id(1L)
			.title("즉시 교환 제목")
			.description("즉시 교환 설명")
			.registeredProduct(registeredProduct)
			.status(TradeStatus.IN_PROGRESS)
			.viewCount(0)
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		List<TradeProduct> tradeProducts = new ArrayList<>();

		tradeProducts.add(TradeProduct.builder()
			.suggestedProduct(SuggestedProduct.builder()
				.status(SuggestedStatus.ACCEPTED)
				.member(member)
				.build())
			.build());

		when(tradeProductRepository.findAllByTradeIdAndTradeType(immediateTrade.getId(), TradeType.IMMEDIATE))
			.thenReturn(tradeProducts);

		doNothing().when(notificationService).saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_CANCEL,
			tradeProducts.get(0).getSuggestedProduct().getMember().getId(),
			TradeType.IMMEDIATE,
			immediateTrade.getId(),
			immediateTrade.getTitle());

		String result = immediateTradeService.cancelAcceptanceOfSuggest(immediateTrade.getId(), verifiedMember);

		assertThat(result).isEqualTo("제안 수락 취소 완료");
		assertThat(immediateTrade.getStatus()).isEqualTo(TradeStatus.PENDING);
		assertThat(tradeProducts.get(0).getSuggestedProduct().getStatus()).isEqualTo(SuggestedStatus.PENDING);
		verify(tradeProductRepository, times(1)).deleteAllInBatch(tradeProducts);

	}

	@Test
	@DisplayName("실패 : 수락하지 않은(IN_PROGRESS가 아닌) 상태에서 제안 취소 메서드 호출")
	void failure() {
		immediateTrade = ImmediateTrade.builder()
			.id(1L)
			.title("즉시 교환 제목")
			.description("즉시 교환 설명")
			.registeredProduct(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		assertThatThrownBy(
			() -> immediateTradeService.cancelAcceptanceOfSuggest(immediateTrade.getId(), verifiedMember))
			.isInstanceOf(ImmediateTradeException.class).hasMessage("IN_PROGRESS 상태의 교환만을 수락 취소할 수 있습니다.");

	}
}
