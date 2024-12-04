package com.barter.domain.trade.periodtrade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.service.PeriodTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PeriodTradeController {

	private final PeriodTradeService periodTradeService;

	@PostMapping("/period-trades")
	public ResponseEntity<CreatePeriodTradeResDto> createPeriodTrades(
		@Valid @RequestBody CreatePeriodTradeReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(periodTradeService.createPeriodTrades(reqDto));
	}

}
