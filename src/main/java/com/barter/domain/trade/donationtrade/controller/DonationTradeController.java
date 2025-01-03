package com.barter.domain.trade.donationtrade.controller;

import static org.springframework.data.domain.Sort.Direction.*;

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

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.trade.donationtrade.dto.request.CreateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.request.UpdateDonationTradeReqDto;
import com.barter.domain.trade.donationtrade.dto.response.CreateDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.FindDonationTradeResDto;
import com.barter.domain.trade.donationtrade.dto.response.SuggestDonationTradeResDto;
import com.barter.domain.trade.donationtrade.service.DonationTradeService;
import com.barter.lock.RedissonLockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donation-trades")
public class DonationTradeController {

	private final DonationTradeService donationTradeService;
	private final RedissonLockService redissonLockService;

	@GetMapping
	public ResponseEntity<PagedModel<FindDonationTradeResDto>> findDonationTrades(
		@PageableDefault(sort = "updatedAt", direction = DESC, size = 15) Pageable pageable
	) {
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
	public ResponseEntity<CreateDonationTradeResDto> createDonationTrade(
		VerifiedMember verifiedMember,
		@RequestBody @Valid CreateDonationTradeReqDto req
	) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(donationTradeService.createDonationTrade(verifiedMember, req));
	}

	@PostMapping("/{tradeId}/suggest")
	public ResponseEntity<SuggestDonationTradeResDto> suggestDonationTrade(
		VerifiedMember verifiedMember,
		@PathVariable("tradeId") Long tradeId
	) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(donationTradeService.suggestDonationTrade(verifiedMember, tradeId));
	}

	@PatchMapping("/{tradeId}")
	public ResponseEntity<Void> updateDonationTrade(
		VerifiedMember verifiedMember,
		@RequestBody UpdateDonationTradeReqDto req,
		@PathVariable("tradeId") Long tradeId
	) {
		donationTradeService.updateDonationTrade(verifiedMember, tradeId, req);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}

	@DeleteMapping("/{tradeId}")
	public ResponseEntity<Void> deleteDonationTrade(
		VerifiedMember verifiedMember,
		@PathVariable("tradeId") Long tradeId
	) {
		donationTradeService.deleteDonationTrade(verifiedMember, tradeId);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}
}
