package com.barter.domain.trade.immediatetrade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.service.NotificationService;
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
import com.barter.exception.customexceptions.ImmediateTradeException;
import com.barter.exception.customexceptions.ProductException;

@ExtendWith(MockitoExtension.class)
public class TradeSuggestTest {

	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	SuggestedProductRepository suggestedProductRepository;
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
	CreateTradeSuggestProductReqDto reqDto;
	SuggestedProduct suggestedProduct;
	SuggestedProduct suggestedProduct2;
	VerifiedMember suggester;
	UpdateStatusReqDto updateStatusReqDto;
	Member member2;

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

		reqDto = new CreateTradeSuggestProductReqDto();
		reqDto.getSuggestedProductIds().addAll(Arrays.asList(1L, 2L));

		member2 = Member.builder()
			.id(2L)
			.email("test")
			.password("1234")
			.build();

		suggestedProduct = SuggestedProduct.builder()
			.id(1L)
			.status(SuggestedStatus.PENDING)
			.member(member2)
			.build();

		suggestedProduct2 = SuggestedProduct.builder()
			.id(2L)
			.status(SuggestedStatus.PENDING)
			.member(member2)
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
	@DisplayName("즉시 교환 - 제안 생성: 실패 - IN_PROGRESS 상태의 교환에 제안")
	void createSuggestFailure() {

		immediateTrade = ImmediateTrade.builder()
			.id(1L)
			.title("즉시 교환 제목")
			.description("즉시 교환 설명")
			.registeredProduct(registeredProduct)
			.status(TradeStatus.IN_PROGRESS)
			.viewCount(0)
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		assertThatThrownBy(() ->
			immediateTradeService.createTradeSuggest(immediateTrade.getId(), reqDto, suggester))
			.isInstanceOf(ImmediateTradeException.class).hasMessage("PENDING 상태의 교환에만 제안할 수 있습니다.");

	}

	@Test
	@DisplayName("즉시 교환 - 제안 생성: 실패 - SUGGESTING 상태의 물품으로 제안")
	void createSuggestFailure2() {

		suggestedProduct = SuggestedProduct.builder()
			.id(1L)
			.status(SuggestedStatus.SUGGESTING)
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));
		when(suggestedProductRepository.findById(1L)).thenReturn(Optional.of(suggestedProduct));

		assertThatThrownBy(() ->
			immediateTradeService.createTradeSuggest(immediateTrade.getId(), reqDto, suggester))
			.isInstanceOf(ProductException.class).hasMessage("PENDING 상태의 상품으로만 제안하실 수 있습니다.");
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

		doNothing().when(notificationService).saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_ACCEPT,
			tradeProducts.get(0).getSuggestedProduct().getMember().getId(),
			TradeType.IMMEDIATE,
			immediateTrade.getId(),
			immediateTrade.getTitle());

		String result = immediateTradeService.acceptTradeSuggest(immediateTrade.getId(), verifiedMember);

		assertThat(result).isEqualTo("제안 수락 완료");

		assertThat(immediateTrade.getStatus()).isEqualTo(TradeStatus.IN_PROGRESS);
		assertThat(suggestedProduct.getStatus()).isEqualTo(SuggestedStatus.ACCEPTED);
		assertThat(suggestedProduct2.getStatus()).isEqualTo(SuggestedStatus.ACCEPTED);

	}

	@Test
	@DisplayName("즉시 교환 - 제안 수락: 실패 - 해당 교환을 찾을 수 없는 경우")
	void acceptSuggestFailure() {

		assertThatThrownBy(() ->
			immediateTradeService.denyTradeSuggest(123L, verifiedMember))
			.isInstanceOf(ImmediateTradeException.class).hasMessage("존재하지 않는 즉시 교환입니다.");

	}

	@Test
	@DisplayName("즉시 교환 - 제안 거절: 성공")
	void denySuggestSuccess() {

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));

		suggestedProduct.changStatusSuggesting();
		suggestedProduct2.changStatusSuggesting();
		immediateTrade.changeStatusInProgress();

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

		doNothing().when(notificationService).saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_DENY,
			tradeProducts.get(0).getSuggestedProduct().getMember().getId(),
			TradeType.IMMEDIATE,
			immediateTrade.getId(),
			immediateTrade.getTitle());

		immediateTradeService.denyTradeSuggest(immediateTrade.getId(), verifiedMember);

		verify(tradeProductRepository, times(1)).deleteAll(any());
	}

	@Test
	@DisplayName("즉시 교환 - 제안 거절: 실패 - 해당 교환을 찾을 수 없는 경우")
	void denySuggestFailure() {

		assertThatThrownBy(() ->
			immediateTradeService.denyTradeSuggest(123L, verifiedMember))
			.isInstanceOf(ImmediateTradeException.class).hasMessage("존재하지 않는 즉시 교환입니다.");

	}

	@Test
	@DisplayName("즉시 교환 - 상태 변경: 성공")
	void updateStatusSuccess() {
		immediateTrade = ImmediateTrade.builder()
			.id(1L)
			.title("즉시 교환 제목")
			.description("즉시 교환 설명")
			.registeredProduct(registeredProduct)
			.status(TradeStatus.IN_PROGRESS)
			.viewCount(0)
			.build();

		when(immediateTradeRepository.findById(immediateTrade.getId())).thenReturn(Optional.ofNullable(immediateTrade));
		when(immediateTradeRepository.save(immediateTrade)).thenReturn(immediateTrade);

		updateStatusReqDto = new UpdateStatusReqDto(TradeStatus.COMPLETED);

		FindImmediateTradeResDto resDto = immediateTradeService.updateStatusCompleted(immediateTrade.getId(),
			updateStatusReqDto,
			verifiedMember);

		assertThat(resDto.getTradeStatus()).isEqualTo(TradeStatus.COMPLETED);

	}
}
