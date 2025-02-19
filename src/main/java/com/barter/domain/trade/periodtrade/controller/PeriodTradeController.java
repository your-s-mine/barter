package com.barter.domain.trade.periodtrade.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.trade.periodtrade.dto.request.AcceptPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.DenyPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.StatusUpdateReqDto;
import com.barter.domain.trade.periodtrade.dto.request.SuggestedPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.UpdatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.response.AcceptPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.DenyPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeSuggestionResDto;
import com.barter.domain.trade.periodtrade.dto.response.StatusUpdateResDto;
import com.barter.domain.trade.periodtrade.dto.response.SuggestedPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.UpdatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.service.PeriodTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PeriodTradeController {

	private final PeriodTradeService periodTradeService;

	@PostMapping("/period-trades")
	public ResponseEntity<CreatePeriodTradeResDto> createPeriodTrades(
		VerifiedMember member,
		@Valid @RequestBody CreatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(periodTradeService.createPeriodTrades(member, reqDto));
	}

	@GetMapping("/period-trades")
	public ResponseEntity<PagedModel<FindPeriodTradeResDto>> findPeriodTrades(
		@PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.findPeriodTrades(pageable)
		);

	}

	@GetMapping("/period-trades/{id}")
	public ResponseEntity<FindPeriodTradeResDto> findPeriodTrade(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.findPeriodTradeById(id)
		);
	}

	@GetMapping("/period-trades/{tradeId}/suggestion") // 제안 온 물품 조회
	public ResponseEntity<List<FindPeriodTradeSuggestionResDto>> findPeriodTradeSuggestion(
		@PathVariable Long tradeId) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.findPeriodTradesSuggestion(tradeId)
		);
	}

	@PatchMapping("/period-trades/{id}")
	public ResponseEntity<UpdatePeriodTradeResDto> updatePeriodTrade(
		VerifiedMember member,
		@PathVariable Long id,
		@Valid @RequestBody UpdatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.updatePeriodTrade(member, id, reqDto)
		);
	}

	@DeleteMapping("/period-trades/{id}")
	public ResponseEntity<Void> deletePeriodTrade(VerifiedMember member, @PathVariable Long id) {
		periodTradeService.deletePeriodTrade(member, id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/period-trades/{id}/suggest") // 등록된 기간 교환에 대한 타 유저의 제안 요청 (최대 제안 물품은 일단 3개)
	public ResponseEntity<SuggestedPeriodTradeResDto> suggestPeriodTrade(
		VerifiedMember member,
		@PathVariable Long id,
		@Valid @RequestBody SuggestedPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.suggestPeriodTrade(member, id, reqDto)
		);
	}

	@PatchMapping("/period-trades/{id}/status")
	public ResponseEntity<StatusUpdateResDto> updatePeriodTradeStatus(
		VerifiedMember member,
		@PathVariable Long id,
		@Valid @RequestBody StatusUpdateReqDto reqDto
	) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.updatePeriodTradeStatus(member, id, reqDto)
		);

	}

	@PatchMapping("/period-trades/{id}/acceptance")
	public ResponseEntity<AcceptPeriodTradeResDto> acceptPeriodTrade(
		VerifiedMember member,
		@PathVariable Long id,
		@Valid @RequestBody AcceptPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.acceptPeriodTrade(member, id, reqDto)
		);
	}

	@PatchMapping("/period-trades/{id}/denial")
	public ResponseEntity<DenyPeriodTradeResDto> denyPeriodTrade(
		VerifiedMember member,
		@PathVariable Long id,
		@Valid @RequestBody DenyPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.denyPeriodTrade(member, id, reqDto)
		);
	}

}
