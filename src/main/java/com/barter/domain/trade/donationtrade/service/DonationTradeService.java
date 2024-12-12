package com.barter.domain.trade.donationtrade.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.FindDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.SuggestDonationTradeResDto;
import com.barter.domain.trade.donationtrade.entity.DonationProductMember;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationProductMemberRepository;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;
import com.barter.domain.trade.enums.DonationResult;
import com.barter.event.trade.TradeNotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationTradeService {

	private final DonationTradeRepository donationTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final DonationProductMemberRepository donationProductMemberRepository;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public void createDonationTrade(Long userId, CreateDonationTradeReqDto req) {
		RegisteredProduct product = registeredProductRepository.findById(req.getProductId())
			.orElseThrow(() -> new IllegalStateException("등록되지 않은 물품입니다."));

		product.validateOwner(userId);
		DonationTrade donationTrade = DonationTrade.createInitDonationTrade(product,
			req.getMaxAmount(),
			req.getTitle(),
			req.getDescription(),
			req.getEndedAt());

		donationTrade.validateIsExceededMaxEndDate();
		DonationTrade savedDonationTrade = donationTradeRepository.save(donationTrade);
		Long savedTradeId = savedDonationTrade.getId();
		String savedTradeProductName = savedDonationTrade.getProduct().getName();
		publisher.publishEvent(new TradeNotificationEvent(savedTradeId, savedTradeProductName));
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
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 나눔 교환 입니다."));
	}

	@Transactional
	public void updateDonationTrade(Long userId, Long tradeId, UpdateDonationTradeReqDto req) {
		DonationTrade donationTrade = donationTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 나눔 교환 입니다."));

		donationTrade.validateUpdate(userId);
		donationTrade.update(req.getTitle(), req.getDescription());
		donationTradeRepository.save(donationTrade);
	}

	@Transactional
	public void deleteDonationTrade(Long userId, Long tradeId) {
		DonationTrade donationTrade = donationTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 나눔 교환 입니다."));

		donationTrade.validateDelete(userId);
		donationTrade.changeProductStatusPending();
		donationTradeRepository.delete(donationTrade);
	}

	@Transactional
	public SuggestDonationTradeResDto suggestDonationTrade(Long userId, Long tradeId) {
		if (donationProductMemberRepository.existsByMemberIdAndDonationTradeId(userId, tradeId)) {
			throw new IllegalStateException("이미 요청한 유저입니다.");
		}
		Member requestMember = memberRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
		DonationTrade donationTrade = donationTradeRepository.findByIdForUpdate(tradeId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 나눔 교환 입니다."));
		if (donationTrade.isDonationCompleted()) {
			return new SuggestDonationTradeResDto("이미 마감된 나눔 입니다.", DonationResult.FAIL);
		}
		donationTrade.suggestDonation();
		DonationProductMember donationProductMember = DonationProductMember.builder()
			.member(requestMember)
			.donationTrade(donationTrade)
			.build();
		donationTradeRepository.save(donationTrade);
		donationProductMemberRepository.save(donationProductMember);
		return new SuggestDonationTradeResDto("나눔 신청 성공", DonationResult.SUCCESS);
	}
}
