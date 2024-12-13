package com.barter.domain.trade.periodtrade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class PeriodTradeServiceTest {

	@InjectMocks
	private PeriodTradeService periodTradeService;

	@Mock
	private RegisteredProductRepository registeredProductRepository;

	@Mock
	private PeriodTradeRepository periodTradeRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private RegisteredProduct registeredProduct;

	private VerifiedMember verifiedMember;
	private Member member;

	@BeforeEach
	void setUp() {

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
			.images(List.of("sample.img"))
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

	@Test
	@DisplayName("기간 교환 생성 시, 등록 되지 않은 물품 예외 테스트")
	public void 기간_교환_생성_시_등록_되지_않은_물품_예외_테스트() {

		//given
		when(registeredProductRepository.findById(any()))
			.thenThrow(new IllegalArgumentException("없는 등록된 물건입니다."));

		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		// when & then
		assertThatThrownBy(() -> periodTradeService.createPeriodTrades(verifiedMember, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("없는 등록된 물건입니다.");

	}

	@Test
	@DisplayName("기간 교환 생성 시 등록 물품에 대한 권한이 없는 경우")
	public void 기간_교환_생성_시_등록_물품에_대한_권한이_없는_경우() {

		//given

		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		RegisteredProduct mockRegisteredProduct = mock(RegisteredProduct.class); // Mock 객체 생성
		// 아래 when 절에서 사용하기 위해서는 Mock 처리된 객체가 사용되어야 하기 때문

		when(registeredProductRepository.findById(any()))
			.thenReturn(Optional.of(mockRegisteredProduct));

		doThrow(new IllegalArgumentException("권한이 없습니다."))
			.when(mockRegisteredProduct).validateOwner(verifiedMember.getId());

		//when & then
		assertThatThrownBy(() -> periodTradeService.createPeriodTrades(verifiedMember, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("권한이 없습니다.");

		verify(registeredProductRepository).findById(reqDto.getRegisteredProductId());
		verify(mockRegisteredProduct).validateOwner(verifiedMember.getId());
	}

}