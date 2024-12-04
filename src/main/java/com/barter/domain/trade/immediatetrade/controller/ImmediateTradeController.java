package com.barter.domain.trade.immediatetrade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/immediate-trades")
public class ImmediateTradeController {

	private final ImmediateTradeService immediateTradeService;

	@PostMapping("")
	public ResponseEntity<String> create(@RequestBody @Valid CreateImmediateTradeReqDto requestDto) {
		return new ResponseEntity<>(immediateTradeService.create(requestDto), HttpStatus.CREATED);
	}

	@GetMapping("")
	public ResponseEntity<String> find(@RequestBody @Valid CreateImmediateTradeReqDto requestDto) {
		return new ResponseEntity<>(immediateTradeService.create(requestDto), HttpStatus.CREATED);
	}
}
