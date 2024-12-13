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
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.periodtrade.dto.request.AcceptPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.DenyPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.SuggestedPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.response.AcceptPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.DenyPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.SuggestedPeriodTradeResDto;
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
	private SuggestedProductRepository suggestedProductRepository;

	@Mock
	private TradeProductRepository tradeProductRepository;

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

	@Test
	@DisplayName("등록된 기간 교환에 대해서 물품을 제안 성공")
	public void 등록된_기간_교환에_대해서_물품을_제안_성공() {

		//given
		Long tradeId = 1L;

		SuggestedPeriodTradeReqDto reqDto = new SuggestedPeriodTradeReqDto(List.of(101L, 102L));

		PeriodTrade mockPeriodTrade = mock(PeriodTrade.class);
		SuggestedProduct product1 = mock(SuggestedProduct.class);
		SuggestedProduct product2 = mock(SuggestedProduct.class);

		when(periodTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockPeriodTrade));

		doNothing().when(mockPeriodTrade).validateSuggestAuthority(member.getId());
		doNothing().when(mockPeriodTrade).validateIsPending();
		doNothing().when(mockPeriodTrade).validateIsCompleted();

		when(suggestedProductRepository.findById(101L)).thenReturn(Optional.of(product1));
		when(suggestedProductRepository.findById(102L)).thenReturn(Optional.of(product2));
		when(product1.getStatus()).thenReturn(SuggestedStatus.PENDING);
		when(product2.getStatus()).thenReturn(SuggestedStatus.PENDING);

		when(tradeProductRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		SuggestedPeriodTradeResDto result = periodTradeService.suggestPeriodTrade(verifiedMember, tradeId, reqDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPeriodTradeId()).isEqualTo(tradeId);
		assertThat(result.getSuggestedProductIds()).hasSize(2);

		verify(periodTradeRepository, times(1)).findById(tradeId);
		verify(mockPeriodTrade, times(1)).validateSuggestAuthority(member.getId());
		verify(mockPeriodTrade, times(1)).validateIsPending();
		verify(mockPeriodTrade, times(1)).validateIsCompleted();
		verify(suggestedProductRepository, times(1)).findById(101L);
		verify(suggestedProductRepository, times(1)).findById(102L);
		verify(tradeProductRepository, times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("기간 거래 제안 실패 (기간 거래 없음)")
	public void 기간_거래_제안_실패_기간_거래_없음() {

		// given
		Long tradeId = 1L;

		SuggestedPeriodTradeReqDto reqDto = new SuggestedPeriodTradeReqDto(List.of(101L, 102L));

		when(periodTradeRepository.findById(tradeId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> periodTradeService.suggestPeriodTrade(verifiedMember, tradeId, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당하는 기간 거래를 찾을 수 없습니다.");

		verify(periodTradeRepository, times(1)).findById(tradeId);
		verifyNoMoreInteractions(periodTradeRepository);
	}

	@Test
	@DisplayName("기간 거래 제안 실패 (제안된 상품 상태 오류)")
	public void 기간_거래_제안_실패_제안_상품_상태_오류() {

		//given
		Long tradeId = 1L;

		SuggestedPeriodTradeReqDto reqDto = new SuggestedPeriodTradeReqDto(List.of(101L));

		PeriodTrade mockPeriodTrade = mock(PeriodTrade.class);
		SuggestedProduct product = mock(SuggestedProduct.class);

		when(periodTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockPeriodTrade));
		when(suggestedProductRepository.findById(101L)).thenReturn(Optional.of(product));
		when(product.getStatus()).thenReturn(SuggestedStatus.ACCEPTED);

		doNothing().when(mockPeriodTrade).validateSuggestAuthority(member.getId());
		doNothing().when(mockPeriodTrade).validateIsPending();
		doNothing().when(mockPeriodTrade).validateIsCompleted();

		// when & then
		assertThatThrownBy(() -> periodTradeService.suggestPeriodTrade(verifiedMember, tradeId, reqDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("다른 교환에 제안된 상품은 제안 할 수 없습니다.");

		verify(periodTradeRepository, times(1)).findById(tradeId);
		verify(suggestedProductRepository, times(1)).findById(101L);

	}

	@Test
	@DisplayName("기간 거래 수락")
	public void 기간_거래_수락() {
		//given
		Long tradeId = 1L;

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(2L);

		AcceptPeriodTradeReqDto reqDto = new AcceptPeriodTradeReqDto(member.getId()); // 다른 멤버 (2L)

		PeriodTrade mockPeriodTrade = mock(PeriodTrade.class);
		TradeProduct tradeProduct1 = mock(TradeProduct.class);
		TradeProduct tradeProduct2 = mock(TradeProduct.class);

		SuggestedProduct suggestedProduct1 = mock(SuggestedProduct.class);
		SuggestedProduct suggestedProduct2 = mock(SuggestedProduct.class);

		RegisteredProduct registeredProduct = mock(RegisteredProduct.class);

		when(mockPeriodTrade.getRegisteredProduct()).thenReturn(registeredProduct);

		when(periodTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockPeriodTrade));

		// 제안된 물건 2개
		when(tradeProductRepository.findAllByTradeIdAndTradeType(tradeId, TradeType.PERIOD))
			.thenReturn(List.of(tradeProduct1, tradeProduct2));

		doNothing().when(mockPeriodTrade).validateAuthority(1L);
		doNothing().when(mockPeriodTrade).validateInProgress();

		doNothing().when(suggestedProduct1).changStatusAccepted();
		doNothing().when(suggestedProduct2).changStatusAccepted();

		doNothing().when(mockPeriodTrade).updatePeriodTradeStatusCompleted();

		when(tradeProduct1.getSuggestedProduct()).thenReturn(suggestedProduct1);
		when(tradeProduct2.getSuggestedProduct()).thenReturn(suggestedProduct2);
		when(suggestedProduct1.getMember()).thenReturn(member);
		when(suggestedProduct2.getMember()).thenReturn(member);

		when(suggestedProduct1.getStatus()).thenReturn(SuggestedStatus.SUGGESTING);
		when(suggestedProduct2.getStatus()).thenReturn(SuggestedStatus.SUGGESTING);

		// when

		AcceptPeriodTradeResDto result = periodTradeService.acceptPeriodTrade(verifiedMember, tradeId, reqDto);

		// then
		assertThat(result).isNotNull();
		verify(periodTradeRepository, times(1)).findById(tradeId);
		verify(tradeProductRepository, times(1)).findAllByTradeIdAndTradeType(tradeId, TradeType.PERIOD);
		verify(mockPeriodTrade, times(1)).validateAuthority(1L);
		verify(mockPeriodTrade, times(1)).validateInProgress();
		verify(suggestedProduct1, times(1)).changStatusAccepted();
		verify(suggestedProduct2, times(1)).changStatusAccepted();
		verify(mockPeriodTrade, times(1)).updatePeriodTradeStatusCompleted();

	}

	@Test
	@DisplayName("기간 거래 거절")
	public void 기간_거래_거절() {
		//given
		Long tradeId = 1L;

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(2L);

		DenyPeriodTradeReqDto reqDto = new DenyPeriodTradeReqDto(member.getId()); // 다른 멤버 (2L)

		PeriodTrade mockPeriodTrade = mock(PeriodTrade.class);
		TradeProduct tradeProduct1 = mock(TradeProduct.class);
		TradeProduct tradeProduct2 = mock(TradeProduct.class);

		SuggestedProduct suggestedProduct1 = mock(SuggestedProduct.class);
		SuggestedProduct suggestedProduct2 = mock(SuggestedProduct.class);

		RegisteredProduct registeredProduct = mock(RegisteredProduct.class);

		when(mockPeriodTrade.getRegisteredProduct()).thenReturn(registeredProduct);

		when(periodTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockPeriodTrade));

		// 제안된 물건 2개
		when(tradeProductRepository.findAllByTradeIdAndTradeType(tradeId, TradeType.PERIOD))
			.thenReturn(List.of(tradeProduct1, tradeProduct2));

		doNothing().when(mockPeriodTrade).validateAuthority(1L);
		doNothing().when(mockPeriodTrade).validateInProgress();

		doNothing().when(suggestedProduct1).changStatusPending();
		doNothing().when(suggestedProduct2).changStatusPending();

		when(tradeProduct1.getSuggestedProduct()).thenReturn(suggestedProduct1);
		when(tradeProduct2.getSuggestedProduct()).thenReturn(suggestedProduct2);
		when(suggestedProduct1.getMember()).thenReturn(member);
		when(suggestedProduct2.getMember()).thenReturn(member);

		when(suggestedProduct1.getStatus()).thenReturn(SuggestedStatus.SUGGESTING);
		when(suggestedProduct2.getStatus()).thenReturn(SuggestedStatus.SUGGESTING);

		// when

		DenyPeriodTradeResDto result = periodTradeService.denyPeriodTrade(verifiedMember, tradeId, reqDto);

		// then
		assertThat(result).isNotNull();
		verify(periodTradeRepository, times(1)).findById(tradeId);
		verify(tradeProductRepository, times(1)).findAllByTradeIdAndTradeType(tradeId, TradeType.PERIOD);
		verify(mockPeriodTrade, times(1)).validateAuthority(1L);
		verify(mockPeriodTrade, times(1)).validateInProgress();
		verify(suggestedProduct1, times(1)).changStatusPending();
		verify(suggestedProduct2, times(1)).changStatusPending();

	}

}