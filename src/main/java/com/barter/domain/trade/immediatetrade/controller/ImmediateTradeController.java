package com.barter.domain.trade.immediatetrade.controller;

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

import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/immediate-trades")
public class ImmediateTradeController {

	private final ImmediateTradeService immediateTradeService;

	// todo: 맴버 받아오면 변수에 추가하기

	@PostMapping("")
	public ResponseEntity<FindImmediateTradeResDto> create(@RequestBody @Valid CreateImmediateTradeReqDto reqDto) {
		return new ResponseEntity<>(immediateTradeService.create(reqDto), HttpStatus.CREATED);
	}

	@GetMapping("{tradeId}")
	public ResponseEntity<FindImmediateTradeResDto> find(@PathVariable Long tradeId) {
		return new ResponseEntity<>(immediateTradeService.find(tradeId), HttpStatus.OK);
	}

	@PatchMapping("/{tradeId}")
	public ResponseEntity<FindImmediateTradeResDto> update(@PathVariable Long tradeId,
		@RequestBody @Valid UpdateImmediateTradeReqDto reqDto) throws IllegalAccessException {
		return new ResponseEntity<>(immediateTradeService.update(tradeId, reqDto), HttpStatus.OK);
	}

	@DeleteMapping("/{tradeId}")
	public ResponseEntity<String> delete(@PathVariable Long tradeId) {
		return new ResponseEntity<>(immediateTradeService.delete(tradeId), HttpStatus.OK);
	}

	@PostMapping("/{tradeId}/suggest")
	public ResponseEntity<String> createSuggest(@PathVariable Long tradeId,
		@RequestBody @Valid CreateTradeSuggestProductReqDto reqDto) {
		return new ResponseEntity<>(immediateTradeService.createSuggest(tradeId, reqDto), HttpStatus.CREATED);
	}
}
