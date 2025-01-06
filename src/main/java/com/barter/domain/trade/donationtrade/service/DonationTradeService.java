package com.barter.domain.trade.donationtrade.service;

import static com.barter.exception.enums.ExceptionCode.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.CreateDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.FindDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.SuggestDonationTradeResDto;
import com.barter.domain.trade.donationtrade.entity.DonationProductMember;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationProductMemberRepository;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.enums.DonationResult;
import com.barter.event.trade.TradeNotificationEvent;
import com.barter.exception.customexceptions.DonationTradeException;
import com.barter.exception.customexceptions.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationTradeService {

	private static final String DONATION_SUGGEST_FAIL_MESSAGE = "이미 마감된 나눔 입니다.";
	private static final String DONATION_SUGGEST_SUCCESS_MESSAGE = "나눔 신청에 성공하였습니다.";
	private final DonationTradeRepository donationTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final DonationProductMemberRepository donationProductMemberRepository;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher publisher;
	private final NotificationService notificationService;

	@Transactional
	public CreateDonationTradeResDto createDonationTrade(VerifiedMember verifiedMember, CreateDonationTradeReqDto req) {
		RegisteredProduct product = registeredProductRepository.findById(req.getProductId())
			.orElseThrow(() -> new IllegalStateException("등록되지 않은 물품입니다."));

		product.validateOwner(verifiedMember.getId());
		product.validatePendingStatusBeforeUpload();
		DonationTrade donationTrade = DonationTrade.createInitDonationTrade(product,
			req.getMaxAmount(),
			req.getTitle(),
			req.getDescription(),
			req.getEndedAt());

		donationTrade.validateExceedMaxEndedAt();
		DonationTrade savedDonationTrade = donationTradeRepository.save(donationTrade);
		publisher.publishEvent(TradeNotificationEvent.builder()
			.tradeId(savedDonationTrade.getId())
			.type(TradeType.DONATION)
			.productName(savedDonationTrade.getProduct().getName())
			.build());
		return CreateDonationTradeResDto.from(savedDonationTrade);
	}

	@Transactional(readOnly = true)
	public PagedModel<FindDonationTradeResDto> findDonationTrades(Pageable pageable) {
		Page<FindDonationTradeResDto> trades = donationTradeRepository.findAll(pageable)
			.map(FindDonationTradeResDto::from);
		return new PagedModel<>(trades);
	}

	@Transactional(readOnly = true)
	public FindDonationTradeResDto findDonationTrade(Long tradeId) {
		return donationTradeRepository.findById(tradeId)
			.map(FindDonationTradeResDto::from)
			.orElseThrow(() -> new DonationTradeException(NOT_FOUND_DONATION_TRADE));
	}

	@Transactional
	public void updateDonationTrade(VerifiedMember verifiedMember, Long tradeId, UpdateDonationTradeReqDto req) {
		DonationTrade donationTrade = donationTradeRepository.findById(tradeId)
			.orElseThrow(() -> new DonationTradeException(NOT_FOUND_DONATION_TRADE));

		donationTrade.validateUpdate(verifiedMember.getId());
		donationTrade.update(req.getTitle(), req.getDescription());
		donationTradeRepository.save(donationTrade);
	}

	@Transactional
	public void deleteDonationTrade(VerifiedMember verifiedMember, Long tradeId) {
		DonationTrade donationTrade = donationTradeRepository.findById(tradeId)
			.orElseThrow(() -> new DonationTradeException(NOT_FOUND_DONATION_TRADE));

		donationTrade.validateDelete(verifiedMember.getId());
		donationTrade.changeProductStatusPending();
		donationTradeRepository.delete(donationTrade);
	}

	@Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW)
	public SuggestDonationTradeResDto suggestDonationTrade(VerifiedMember verifiedMember, Long tradeId) {
		if (donationProductMemberRepository.existsByMemberIdAndDonationTradeId(verifiedMember.getId(), tradeId)) {
			throw new IllegalStateException("이미 요청한 유저입니다.");
		}
		Member requestMember = memberRepository.findById(verifiedMember.getId())
			.orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
		DonationTrade donationTrade = donationTradeRepository.findById(tradeId)
			.orElseThrow(() -> new DonationTradeException(NOT_FOUND_DONATION_TRADE));
		if (donationTrade.isDonationCompleted()) {
			return new SuggestDonationTradeResDto(DONATION_SUGGEST_FAIL_MESSAGE, DonationResult.FAIL);
		}
		donationTrade.suggestDonation();
		DonationProductMember donationProductMember = DonationProductMember.builder()
			.member(requestMember)
			.donationTrade(donationTrade)
			.build();
		donationTradeRepository.save(donationTrade);
		donationProductMemberRepository.save(donationProductMember);

		// 이벤트 정보 저장 및 전달
		notificationService.saveTradeNotification(
			EventKind.DONATION_TRADE_SUGGEST, donationTrade.getProduct().getMember().getId(),
			TradeType.DONATION, donationTrade.getId(), donationTrade.getTitle()
		);
		return new SuggestDonationTradeResDto(DONATION_SUGGEST_SUCCESS_MESSAGE, DonationResult.SUCCESS);
	}
}
