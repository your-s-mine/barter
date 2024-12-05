package com.barter.domain.trade.donationtrade.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.FindDonationTradeResDto;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationTradeService {

	private final DonationTradeRepository donationTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;

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
		donationTradeRepository.save(donationTrade);
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
}
