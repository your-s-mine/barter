package com.barter.domain.trade.donationtrade.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.FindDonationTradeResDto;
import com.barter.domain.trade.donationtrade.service.DonationTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donation-trades")
public class DonationTradeController {

	private final DonationTradeService donationTradeService;

	@GetMapping
	public ResponseEntity<PagedModel<FindDonationTradeResDto>> findDonationTrades(@PageableDefault Pageable pageable) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(donationTradeService.findDonationTrades(pageable));
	}

	@GetMapping("/{tradeId}")
	public ResponseEntity<FindDonationTradeResDto> findDonationTrade(@PathVariable("tradeId") Long tradeId) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(donationTradeService.findDonationTrade(tradeId));
	}

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

	@PatchMapping("/{tradeId}")
	public ResponseEntity<Void> updateDonationTrade(
		@RequestBody UpdateDonationTradeReqDto req,
		@PathVariable("tradeId") Long tradeId
	) {
		// TODO: 인증된 Member 받아오는 기능 추가 필요
		Long userId = 1L;
		donationTradeService.updateDonationTrade(userId, tradeId, req);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}

	@DeleteMapping("/{tradeId}")
	public ResponseEntity<Void> deleteDonationTrade(
		@PathVariable("tradeId") Long tradeId
	) {
		// TODO: 인증된 Member 받아오는 기능 추가 필요
		Long userId = 1L;
		donationTradeService.deleteDonationTrade(userId, tradeId);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}
}
