package com.barter.domain.trade.periodtrade.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.service.PeriodTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PeriodTradeController {

	private final PeriodTradeService periodTradeService;

	// TODO : 아래 모든 컨트롤러에 유저 정보가 포함되어야 한다.

	@PostMapping("/period-trades")
	public ResponseEntity<CreatePeriodTradeResDto> createPeriodTrades(
		@Valid @RequestBody CreatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(periodTradeService.createPeriodTrades(reqDto));
	}

	@GetMapping("/period-trades")
	public ResponseEntity<List<FindPeriodTradeResDto>> findPeriodTrades() {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.findPeriodTrades()
		);

	}

	@GetMapping("/period-trades/{id}")
	public ResponseEntity<FindPeriodTradeResDto> findPeriodTrade(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(
			periodTradeService.findPeriodTradeById(id)
		);
	}

}
