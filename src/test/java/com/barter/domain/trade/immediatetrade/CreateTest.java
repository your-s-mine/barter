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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.barter.domain.member.entity.Address;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;
import com.barter.event.trade.TradeNotificationEvent;
import com.barter.exception.customexceptions.ImmediateTradeException;

@ExtendWith(MockitoExtension.class)
public class CreateTest {

	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	RegisteredProductRepository registeredProductRepository;
	@Mock
	ApplicationEventPublisher publisher;
	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	CreateRegisteredProductReqDto createRegisteredProductReqDto;
	RegisteredProduct registeredProduct;
	CreateImmediateTradeReqDto createImmediateTradeReqDto;
	ImmediateTrade immediateTrade;

	@BeforeEach
	void setUp() {
		member = Member.createBasicMember("test@test.com", "1234", "test", Address.builder().build());
		createRegisteredProductReqDto = new CreateRegisteredProductReqDto(
			"등록 상품 제목", "등록 상품 설명");

		List<String> images = new ArrayList<>();
		images.add("testImage");

		registeredProduct = RegisteredProduct.builder()
			.id(1L)
			.member(member)
			.build();

		createImmediateTradeReqDto = CreateImmediateTradeReqDto.builder()
			.registeredProductId(registeredProduct.getId())
			.title("제목")
			.description("설명")
			.build();

		immediateTrade = ImmediateTrade.builder()
			.title(createImmediateTradeReqDto.getTitle())
			.description(createImmediateTradeReqDto.getDescription())
			.registeredProduct(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();
	}

	@Test
	@DisplayName("즉시 교환 생성: 성공")
	void success() {
		// given
		when(registeredProductRepository.findById(createImmediateTradeReqDto.getRegisteredProductId()))
			.thenReturn(Optional.ofNullable(registeredProduct));

		when(immediateTradeRepository.save(any())).thenReturn(immediateTrade);

		ArgumentCaptor<TradeNotificationEvent> eventCaptor = ArgumentCaptor.forClass(TradeNotificationEvent.class);
		doNothing().when(publisher).publishEvent(eventCaptor.capture());

		// when
		FindImmediateTradeResDto resDto = immediateTradeService.create(createImmediateTradeReqDto);

		// then
		assertThat(resDto.getTitle()).isEqualTo("제목");
		assertThat(resDto.getDescription()).isEqualTo("설명");
		assertThat(resDto.getRegisterProductId()).isEqualTo(registeredProduct.getId());

		TradeNotificationEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.getType()).isEqualTo(TradeType.IMMEDIATE);
		assertThat(capturedEvent.getProductName()).isEqualTo(registeredProduct.getName());
		verify(publisher, times(1)).publishEvent(any(TradeNotificationEvent.class));
	}

	@Test
	@DisplayName("즉시 교환 생성: 실패 - 등록 물품을 찾을 수 없는 경우")
	void failure() {
		// given
		when(registeredProductRepository.findById(createImmediateTradeReqDto.getRegisteredProductId()))
			.thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> immediateTradeService.create(createImmediateTradeReqDto))
			.isInstanceOf(ImmediateTradeException.class);
	}
}
