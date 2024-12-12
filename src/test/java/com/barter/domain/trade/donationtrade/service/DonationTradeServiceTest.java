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
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.CreateDonationTradeResDto;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationProductMemberRepository;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;

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
			.isInstanceOf(IllegalArgumentException.class)
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
			.isInstanceOf(IllegalArgumentException.class)
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
}