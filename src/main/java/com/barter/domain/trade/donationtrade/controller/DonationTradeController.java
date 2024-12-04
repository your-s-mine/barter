package com.barter.domain.trade.donationtrade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.trade.donationtrade.dto.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.service.DonationTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donation-trades")
public class DonationTradeController {

	private final DonationTradeService donationTradeService;

	@PostMapping
	public ResponseEntity<Void> createDonationTrade(
		@RequestBody @Valid CreateDonationTradeReqDto req
	) {
		// TODO: 인증된 Member 받아오는 기능 추가 필요
		Long userId = 1L;
		donationTradeService.createDonationTrade(userId, req);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}
}
