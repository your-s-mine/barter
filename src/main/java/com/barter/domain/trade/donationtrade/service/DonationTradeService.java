package com.barter.domain.trade.donationtrade.service;

import org.springframework.stereotype.Service;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.donationtrade.dto.CreateDonationTradeReqDto;
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
}
