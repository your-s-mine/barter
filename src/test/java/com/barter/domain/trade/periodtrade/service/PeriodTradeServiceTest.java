package com.barter.domain.trade.periodtrade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.enums.JoinPath;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.periodtrade.dto.request.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.response.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;
import com.barter.event.trade.PeriodTradeEvent.PeriodTradeCloseEvent;

class PeriodTradeServiceTest {

	@InjectMocks
	private PeriodTradeService periodTradeService;

	@Mock
	private RegisteredProductRepository registeredProductRepository;

	@Mock
	private PeriodTradeRepository periodTradeRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	private VerifiedMember verifiedMember;
	private RegisteredProduct registeredProduct;
	private Member member;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		verifiedMember = new VerifiedMember(1L, "test@email.com");

		member = Member.builder() // 물건 register(등록) 한 멤버
			.id(1L)
			.email("test@email.com")
			.nickname("100th member")
			.password("sample123")
			.joinPath(JoinPath.BASIC)
			.build();

		registeredProduct = RegisteredProduct.builder()
			.name("골동품")
			.description("100년 전통의 가구")
			.images("sample.img")
			.status(RegisteredStatus.PENDING)
			.member(member)
			.build();
	}

	@Test
	@DisplayName("기간교환 생성 api 테스트")
	void createPeriodTradeTest() {

		// given
		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		when(registeredProductRepository.findById(reqDto.getRegisteredProductId()))
			.thenReturn(Optional.of(registeredProduct));

		// when

		CreatePeriodTradeResDto response = periodTradeService.createPeriodTrades(verifiedMember, reqDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(reqDto.getTitle());
		verify(registeredProductRepository).findById(reqDto.getRegisteredProductId());
		verify(periodTradeRepository).save(any(PeriodTrade.class));
		verify(eventPublisher).publishEvent(any(PeriodTradeCloseEvent.class));
	}

}