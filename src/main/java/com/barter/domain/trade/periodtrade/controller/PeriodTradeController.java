package com.barter.domain.trade.periodtrade.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.member.entity.Member;
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

	// TODO : 아래 모든 컨트롤러에 유저 정보가 포함되어야 한다.
	// VerifiedMember member 형식으로 유저 정보가들어올 예정 (정보 : id, email, nickname)
	// 일단 Member 로 하자

	@PostMapping("/period-trades")
	public ResponseEntity<CreatePeriodTradeResDto> createPeriodTrades(
		Member member,
		@Valid @RequestBody CreatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(periodTradeService.createPeriodTrades(member, reqDto));
	}

	@GetMapping("/period-trades")
	public ResponseEntity<PagedModel<FindPeriodTradeResDto>> findPeriodTrades(@PageableDefault Pageable pageable) {
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

	@PatchMapping("/period-trades/{id}")
	public ResponseEntity<UpdatePeriodTradeResDto> updatePeriodTrade(
		@PathVariable Long id,
		@Valid @RequestBody UpdatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.updatePeriodTrade(id, reqDto)
		);
	}

	@DeleteMapping("/period-trades/{id}")
	public ResponseEntity<Void> deletePeriodTrade(@PathVariable Long id) {
		periodTradeService.deletePeriodTrade(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/period-trades/{id}/suggest") // 등록된 기간 교환에 대한 타 유저의 제안 요청 (최대 제안 물품은 일단 3개)
	public ResponseEntity<SuggestedPeriodTradeResDto> suggestPeriodTrade(
		@PathVariable Long id,
		@Valid @RequestBody SuggestedPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.suggestPeriodTrade(id, reqDto)
		);
	}

	@PatchMapping("/period-trades/{id}/status")
	public ResponseEntity<StatusUpdateResDto> updatePeriodTradeStatus(
		@PathVariable Long id,
		@Valid @RequestBody StatusUpdateReqDto reqDto
	) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.updatePeriodTradeStatus(id, reqDto)
		);

	}

	@PatchMapping("/period-trades/{id}/acceptance")
	public ResponseEntity<AcceptPeriodTradeResDto> acceptPeriodTrade(
		@PathVariable Long id,
		@Valid @RequestBody AcceptPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.acceptPeriodTrade(id, reqDto)
		);
	}

	@PatchMapping("/period-trades/{id}/denial")
	public ResponseEntity<DenyPeriodTradeResDto> denyPeriodTrade(
		@PathVariable Long id,
		@Valid @RequestBody DenyPeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.denyPeriodTrade(id, reqDto)
		);
	}

}
