package com.barter.domain.trade.periodtrade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.enums.JoinPath;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.dto.request.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.response.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;
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

	@Test
	@DisplayName("기간 교환 생성 시 유효하지 않은 날짜 등록")
	public void 기간_교환_생성_시_유효하지_않은_날짜_등록() {

		// given
		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().plusDays(10)) // 7일 초과
			.build();

		when(registeredProductRepository.findById(reqDto.getRegisteredProductId()))
			.thenReturn(Optional.of(registeredProduct));

		// when &
		assertThatThrownBy(() -> periodTradeService.createPeriodTrades(verifiedMember, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("종료일자는 오늘로부터 7일 이내만 가능합니다.");

	}

	@Test
	@DisplayName("기간 교환 생성 시 이전 날짜 날짜 등록")
	public void 기간_교환_생성_시_이전_날짜_등록() {

		// given
		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().minusDays(1)) // 현재 보다 이전
			.build();

		when(registeredProductRepository.findById(reqDto.getRegisteredProductId()))
			.thenReturn(Optional.of(registeredProduct));

		// when &
		assertThatThrownBy(() -> periodTradeService.createPeriodTrades(verifiedMember, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("현재 시간보다 적은 시간 예약은 불가능 합니다.");

	}

	@Test
	@DisplayName("기간 교환 생성 시 이미 다른 교환에 등록 중인 상품 등록")
	public void 기간_교환_생성_시_이미_다른_교환에_등록_중인_상품_등록() {

		// given
		CreatePeriodTradeReqDto reqDto = CreatePeriodTradeReqDto.builder()
			.title("test title")
			.description("test description")
			.registeredProductId(registeredProduct.getId())
			.endedAt(LocalDateTime.now().plusDays(7))
			.build();

		registeredProduct.updateStatus(RegisteredStatus.REGISTERING.toString());

		when(registeredProductRepository.findById(reqDto.getRegisteredProductId()))
			.thenReturn(Optional.of(registeredProduct));

		// when & then
		assertThatThrownBy(() -> periodTradeService.createPeriodTrades(verifiedMember, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("PENDING 상태만 업로드 가능합니다.");

	}

	// -----------------------------------------------------------------
	@Test
	@DisplayName("기간 교환 다건 조회")
	public void 기간_교환_다건_조회() {

		//given
		Pageable pageable = PageRequest.of(0, 10);

		PeriodTrade periodTrade = PeriodTrade.builder()
			.title("test title")
			.description("test description")
			.status(TradeStatus.PENDING)
			.product(registeredProduct)
			.viewCount(0)
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		FindPeriodTradeResDto resDto1 = FindPeriodTradeResDto.from(periodTrade);
		FindPeriodTradeResDto resDto2 = FindPeriodTradeResDto.from(periodTrade);

		List<FindPeriodTradeResDto> tradeList = Arrays.asList(resDto1, resDto2);

		Page mockPage = mock(Page.class);
		when(mockPage.getContent()).thenReturn(tradeList);
		when(periodTradeRepository.findAll(pageable)).thenReturn(mockPage);
		when(mockPage.map(any())).thenReturn(mockPage);

		// when
		PagedModel<FindPeriodTradeResDto> result = periodTradeService.findPeriodTrades(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).containsExactly(resDto1, resDto2);
		verify(periodTradeRepository, times(1)).findAll(pageable);
		verify(mockPage, times(1)).map(any());

	}

	@Test
	@DisplayName("기간 교환 단건 조회")
	public void 기간_교환_단건_조회() {

		// given
		Long id = 1L;

		PeriodTrade periodTrade = PeriodTrade.builder()
			.title("test title")
			.description("test description")
			.status(TradeStatus.PENDING)
			.product(registeredProduct)
			.viewCount(0)
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		FindPeriodTradeResDto resDto = FindPeriodTradeResDto.from(periodTrade);

		when(periodTradeRepository.findById(id)).thenReturn(Optional.of(periodTrade));

		// when
		FindPeriodTradeResDto result = periodTradeService.findPeriodTradeById(id);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(resDto.getTitle());
		assertThat(periodTrade.getViewCount()).isEqualTo(1);
		verify(periodTradeRepository, times(1)).findById(id);

	}

}