package com.barter.domain.trade.donationtrade.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.CreateDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.SuggestDonationTradeResDto;
import com.barter.domain.trade.donationtrade.entity.DonationProductMember;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationProductMemberRepository;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.enums.DonationResult;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.customexceptions.DonationTradeException;
import com.barter.exception.customexceptions.MemberException;

@ExtendWith(MockitoExtension.class)
class DonationTradeServiceTest {

	@Mock
	private DonationTradeRepository donationTradeRepository;
	@Mock
	private RegisteredProductRepository registeredProductRepository;
	@Mock
	private DonationProductMemberRepository donationProductMemberRepository;
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private NotificationService notificationService;
	@Mock
	private ApplicationEventPublisher publisher;

	@InjectMocks
	private DonationTradeService donationTradeService;

	// 1-2. 등록되지 않은 물품일 때 예외 테스트
	@Test
	@DisplayName("나눔 교환 생성 시, 등록되지 않은 물품일 때 예외 테스트")
	public void 나눔_교환_생성_시_등록되지_않은_물품_예외_테스트() {
		//given
		when(registeredProductRepository.findById(any()))
			.thenThrow(new IllegalArgumentException("등록되지 않은 물품입니다."));
		VerifiedMember verifiedMember = VerifiedMember.builder().build();
		CreateDonationTradeReqDto req = CreateDonationTradeReqDto.builder()
			.productId(1L)
			.build();

		//when && then
		assertThatThrownBy(() -> donationTradeService.createDonationTrade(verifiedMember, req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("등록되지 않은 물품입니다.");
	}

	// 1-3. 등록한 물품의 멤버와 나눔 생성하려는 멤버가 다를 때 예외 테스트
	@Test
	@DisplayName("등록한 물품의 멤버와 나눔 생성하려는 멤버가 다를 때 예외 테스트")
	public void 등록한_물품의_멤버와_나눔_생성하려는_멤버가_다를_때_예외_테스트() {
		//given
		when(registeredProductRepository.findById(any()))
			.thenReturn(Optional.of(RegisteredProduct.builder()
				.member(Member.builder()
					.id(1L)
					.build())
				.status(RegisteredStatus.REGISTERING)
				.build()));
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(2L)
			.build();
		CreateDonationTradeReqDto req = CreateDonationTradeReqDto.builder()
			.productId(1L)
			.build();

		//when && then
		assertThatThrownBy(() -> donationTradeService.createDonationTrade(verifiedMember, req))
			.isInstanceOf(AuthException.class)
			.hasMessageContaining("권한이 없습니다.");
	}

	// 1-4. 등록한 교환의 기간이 7일보다 길 때 예외 테스트
	@Test
	@DisplayName("등록한 교환의 기간이 7일보다 길 때 예외 테스트")
	public void 등록한_교환의_기간이_7일보다_길_때_예외_테스트() {
		//given
		when(registeredProductRepository.findById(any()))
			.thenReturn(Optional.of(RegisteredProduct.builder()
				.member(Member.builder()
					.id(1L)
					.build())
				.status(RegisteredStatus.PENDING)
				.build()));
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateDonationTradeReqDto req = CreateDonationTradeReqDto.builder()
			.productId(1L)
			.title("")
			.description("")
			.maxAmount(3)
			.endedAt(LocalDateTime.now().plusDays(10))
			.build();

		//when && then
		assertThatThrownBy(() -> donationTradeService.createDonationTrade(verifiedMember, req))
			.isInstanceOf(DonationTradeException.class)
			.hasMessageContaining("종료일자는 오늘로부터 7일 이내만 가능합니다.");
	}

	@Test
	@DisplayName("나눔 교환 생성 시 물품 상태 예외 테스트")
	public void 나눔_교환_생성_시_물품_상태_예외_테스트() {
		//given
		when(registeredProductRepository.findById(any()))
			.thenReturn(Optional.of(RegisteredProduct.builder()
				.member(Member.builder()
					.id(1L)
					.build())
				.status(RegisteredStatus.REGISTERING)
				.build()));
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateDonationTradeReqDto req = CreateDonationTradeReqDto.builder()
			.productId(1L)
			.title("")
			.description("")
			.maxAmount(3)
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		//when && then
		assertThatThrownBy(() -> donationTradeService.createDonationTrade(verifiedMember, req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("PENDING 상태만 업로드 가능합니다.");
	}

	// 1-5. 생성 성공 테스트 (반환된 DonationTrade의 title, product가 등록한 것과 같은지 검증)
	@Test
	@DisplayName("나눔 교환 생성 성공 테스트")
	public void 나눔_교환_생성_성공_테스트() {
		//given
		when(registeredProductRepository.findById(any()))
			.thenReturn(Optional.of(RegisteredProduct.builder()
				.member(Member.builder()
					.id(1L)
					.build())
				.status(RegisteredStatus.PENDING)
				.build()));
		when(donationTradeRepository.save(any()))
			.thenReturn(DonationTrade.builder()
				.id(1L)
				.title("title")
				.product(RegisteredProduct.builder()
					.id(1L)
					.member(Member.builder()
						.id(1L)
						.build())
					.status(RegisteredStatus.PENDING)
					.build())
				.description("description")
				.build());

		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateDonationTradeReqDto req = CreateDonationTradeReqDto.builder()
			.productId(1L)
			.title("title")
			.description("description")
			.maxAmount(3)
			.endedAt(LocalDateTime.now().plusDays(5))
			.build();

		//when
		CreateDonationTradeResDto result = donationTradeService.createDonationTrade(verifiedMember, req);

		//then
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getProductId()).isEqualTo(1L);
		assertThat(result.getTitle()).isEqualTo("title");
		assertThat(result.getDescription()).isEqualTo("description");
	}

	@Test
	@DisplayName("나눔 거래 수정 정상 테스트")
	void 나눔_거래_수정_정상_테스트() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		Long tradeId = 100L;
		UpdateDonationTradeReqDto req = UpdateDonationTradeReqDto.builder()
			.title("title")
			.description("description")
			.build();

		DonationTrade donationTrade = mock(DonationTrade.class);
		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		doNothing().when(donationTrade).validateUpdate(verifiedMember.getId());
		doNothing().when(donationTrade).update(req.getTitle(), req.getDescription());
		when(donationTradeRepository.save(any(DonationTrade.class))).thenReturn(donationTrade);

		// when
		donationTradeService.updateDonationTrade(verifiedMember, tradeId, req);

		// then
		verify(donationTradeRepository, times(1)).findById(tradeId);
		verify(donationTrade, times(1)).validateUpdate(verifiedMember.getId());
		verify(donationTrade, times(1)).update("title", "description");
		verify(donationTradeRepository, times(1)).save(donationTrade);
	}

	@Test
	@DisplayName("존재하지 않는 나눔 교환 조회 시 예외 발생")
	void 존재하지_않는_나눔_교환_조회_시_예외_발생() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "member");
		Long tradeId = 999L;
		UpdateDonationTradeReqDto req = new UpdateDonationTradeReqDto("title", "description");

		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.empty());

		// when && then
		assertThatThrownBy(() -> donationTradeService.updateDonationTrade(verifiedMember, tradeId, req))
			.isInstanceOf(DonationTradeException.class)
			.hasMessageContaining("존재하지 않는 나눔 교환");
	}

	@Test
	@DisplayName("수정 권한 예외 테스트")
	void 수정_권한_예외_테스트() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(2L, "member2");
		Long tradeId = 100L;
		UpdateDonationTradeReqDto req = new UpdateDonationTradeReqDto("title", "description");

		DonationTrade donationTrade = mock(DonationTrade.class);
		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		doThrow(new IllegalStateException("권한이 없습니다."))
			.when(donationTrade).validateUpdate(verifiedMember.getId());

		// when && then
		assertThatThrownBy(() -> donationTradeService.updateDonationTrade(verifiedMember, tradeId, req))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("권한이 없습니다.");
	}

	@Test
	@DisplayName("나눔 교환 삭제 정상 테스트")
	void 나눔_교환_정상_삭제_정상_테스트() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "member");
		Long tradeId = 100L;
		DonationTrade donationTrade = mock(DonationTrade.class);

		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		doNothing().when(donationTrade).validateDelete(verifiedMember.getId());
		doNothing().when(donationTrade).changeProductStatusPending();

		// when
		donationTradeService.deleteDonationTrade(verifiedMember, tradeId);

		// when && then
		verify(donationTradeRepository, times(1)).findById(tradeId);
		verify(donationTrade, times(1)).validateDelete(verifiedMember.getId());
		verify(donationTrade, times(1)).changeProductStatusPending();
		verify(donationTradeRepository, times(1)).delete(donationTrade);
	}

	@Test
	@DisplayName("존재하지 않는 나눔 거래 삭제 시 예외 발생 테스트")
	void 존재하지_않는_나눔_거래_삭제_시_예외_발생_테스트() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "member");
		Long tradeId = 999L;

		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.empty());

		// when && then
		assertThatThrownBy(() -> donationTradeService.deleteDonationTrade(verifiedMember, tradeId))
			.isInstanceOf(DonationTradeException.class)
			.hasMessageContaining("존재하지 않는 나눔 교환");
	}

	@Test
	@DisplayName("나눔 교환 삭제 권한 예외 테스트")
	void 나눔_교환_삭제_권한_예외_테스트() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(2L, "member");
		Long tradeId = 100L;
		DonationTrade donationTrade = mock(DonationTrade.class);

		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		doThrow(new IllegalStateException("삭제 권한이 없습니다."))
			.when(donationTrade).validateDelete(verifiedMember.getId());

		// when && then
		assertThatThrownBy(() -> donationTradeService.deleteDonationTrade(verifiedMember, tradeId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("삭제 권한이 없습니다.");
	}

	@Test
	@DisplayName("나눔 교환 제안 이미 요청한 유저일 경우 예외 발생")
	void 나눔_교환_제안_이미_요청한_유저일_경우_예외_발생() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "tester");
		Long tradeId = 100L;

		when(donationProductMemberRepository.existsByMemberIdAndDonationTradeId(1L, 100L)).thenReturn(true);

		// when && then
		assertThatThrownBy(() -> donationTradeService.suggestDonationTrade(verifiedMember, tradeId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("이미 요청한 유저입니다.");
	}

	@Test
	@DisplayName("나눔 교환 제안 존재하지 않는 유저 예외 발생")
	void 나눔_교환_제안_존재하지_않는_유저_예외_발생() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "tester");
		Long tradeId = 100L;

		when(donationProductMemberRepository.existsByMemberIdAndDonationTradeId(1L, 100L)).thenReturn(false);
		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		// when && then
		assertThatThrownBy(() -> donationTradeService.suggestDonationTrade(verifiedMember, tradeId))
			.isInstanceOf(MemberException.class)
			.hasMessageContaining("존재하지 않는 멤버입니다.");
	}

	@Test
	@DisplayName("나눔 제안 존재하지_않는_나눔_거래_예외_발생")
	void 존재하지_않는_나눔_거래_예외_발생() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "tester");
		Long tradeId = 100L;
		Member member = mock(Member.class);

		when(donationProductMemberRepository.existsByMemberIdAndDonationTradeId(1L, 100L)).thenReturn(false);
		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.empty());

		// when && then
		assertThatThrownBy(() -> donationTradeService.suggestDonationTrade(verifiedMember, tradeId))
			.isInstanceOf(DonationTradeException.class)
			.hasMessageContaining("존재하지 않는 나눔 교환");
	}

	@Test
	@DisplayName("마감된 나눔 거래일 경우 FAIL 응답")
	void 마감된_나눔_거래일_경우_FAIL_응답() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "tester");
		Long tradeId = 100L;
		Member member = mock(Member.class);
		DonationTrade donationTrade = mock(DonationTrade.class);

		when(donationProductMemberRepository.existsByMemberIdAndDonationTradeId(1L, 100L)).thenReturn(false);
		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		when(donationTrade.isDonationCompleted()).thenReturn(true);

		// when
		SuggestDonationTradeResDto result = donationTradeService.suggestDonationTrade(verifiedMember, tradeId);

		// then
		assertThat(result.getMessage()).isEqualTo("이미 마감된 나눔 입니다.");
		assertThat(result.getResult()).isEqualTo(DonationResult.FAIL);
		verify(donationTradeRepository, never()).save(any());
		verify(donationProductMemberRepository, never()).save(any());
	}

	@Test
	@DisplayName("나눔 신청 성공")
	void 나눔_신청_성공() {
		// given
		VerifiedMember verifiedMember = new VerifiedMember(1L, "tester");
		Long tradeId = 100L;
		Member member = Member.builder()
			.id(2L)
			.build();
		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.member(member)
			.build();
		DonationTrade donationTrade = DonationTrade.builder()
			.product(registeredProduct)
			.maxAmount(1)
			.status(TradeStatus.PENDING)
			.build();

		when(donationProductMemberRepository.existsByMemberIdAndDonationTradeId(1L, 100L)).thenReturn(false);
		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(donationTradeRepository.findById(tradeId)).thenReturn(Optional.of(donationTrade));
		when(donationTradeRepository.save(donationTrade)).thenReturn(donationTrade);
		doNothing().when(notificationService).saveTradeNotification(any(), any(), any(), any(), any());

		// when
		SuggestDonationTradeResDto result = donationTradeService.suggestDonationTrade(verifiedMember, tradeId);

		// then
		assertThat(result.getMessage()).isEqualTo("나눔 신청에 성공하였습니다.");
		assertThat(result.getResult()).isEqualTo(DonationResult.SUCCESS);
		verify(donationTradeRepository, times(1)).save(donationTrade);
		verify(donationProductMemberRepository, times(1)).save(any(DonationProductMember.class));
	}
}