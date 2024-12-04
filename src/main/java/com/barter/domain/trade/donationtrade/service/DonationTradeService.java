package com.barter.domain.trade.donationtrade.service;

import org.springframework.stereotype.Service;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.donationtrade.dto.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.donationtrade.repository.DonationTradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationTradeService {

	private final DonationTradeRepository donationTradeRepository;
	// TODO: private final RegisteredProductRepository registeredProductRepository;

	public void createDonationTrade(Long userId, CreateDonationTradeReqDto req) {
		// TODO: RegisteredProductRepository 병합 되면 Product검증 코드 작성
		//  RegisteredProduct product = registeredProductRepository.findById(req.getProductId())
		// 	.orElse(() -> new IllegalStateException("등록되지 않은 물품입니다."));

		// TODO: 검증된 Product 객체 가져오게되면 추후 제거 예정
		RegisteredProduct product = null;
		DonationTrade donationTrade = DonationTrade.createInitDonationTrade(product,
			req.getMaxAmount(),
			req.getTitle(),
			req.getDescription(),
			req.getEndedAt());

		donationTrade.validateIsExceededMaxEndDate();
		donationTradeRepository.save(donationTrade);
	}
}
