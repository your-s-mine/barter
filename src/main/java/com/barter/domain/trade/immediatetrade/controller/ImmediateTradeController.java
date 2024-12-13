package com.barter.domain.trade.immediatetrade.controller;

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
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateStatusReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/immediate-trades")
public class ImmediateTradeController {

	private final ImmediateTradeService immediateTradeService;

	@PostMapping("")
	public ResponseEntity<FindImmediateTradeResDto> create(@RequestBody @Valid CreateImmediateTradeReqDto reqDto) {
		return new ResponseEntity<>(immediateTradeService.create(reqDto), HttpStatus.CREATED);
	}

	@GetMapping("{tradeId}")
	public ResponseEntity<FindImmediateTradeResDto> find(@PathVariable Long tradeId) {
		return new ResponseEntity<>(immediateTradeService.find(tradeId), HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<PagedModel<FindImmediateTradeResDto>> findImmediateTrades(
		@PageableDefault Pageable pageable) {
		return new ResponseEntity<>(immediateTradeService.findImmediateTrades(pageable), HttpStatus.OK);
	}

	@PatchMapping("/{tradeId}")
	public ResponseEntity<FindImmediateTradeResDto> update(@PathVariable Long tradeId,
		@RequestBody @Valid UpdateImmediateTradeReqDto reqDto, VerifiedMember member) throws IllegalAccessException {
		return new ResponseEntity<>(immediateTradeService.update(member, tradeId, reqDto), HttpStatus.OK);
	}

	@DeleteMapping("/{tradeId}")
	public ResponseEntity<String> delete(@PathVariable Long tradeId, VerifiedMember member) {
		return new ResponseEntity<>(immediateTradeService.delete(tradeId, member), HttpStatus.OK);
	}

	@PostMapping("/{tradeId}/suggest")
	public ResponseEntity<String> createSuggest(@PathVariable Long tradeId,
		@RequestBody @Valid CreateTradeSuggestProductReqDto reqDto, VerifiedMember member) {
		return new ResponseEntity<>(immediateTradeService.createTradeSuggest(tradeId, reqDto, member),
			HttpStatus.CREATED);
	}

	@PatchMapping("/{tradeId}/acceptance")
	public ResponseEntity<String> acceptTradeSuggest(@PathVariable Long tradeId, VerifiedMember member) {
		return new ResponseEntity<>(immediateTradeService.acceptTradeSuggest(tradeId, member), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/{tradeId}/denial")
	public ResponseEntity<String> denyTradeSuggest(@PathVariable Long tradeId, VerifiedMember member) {
		return new ResponseEntity<>(immediateTradeService.denyTradeSuggest(tradeId, member), HttpStatus.OK);
	}

	@PatchMapping("/status/{tradeId}")
	public ResponseEntity<FindImmediateTradeResDto> updateStatus(@PathVariable Long tradeId,
		@Valid @RequestBody UpdateStatusReqDto reqDto, VerifiedMember member) {
		return new ResponseEntity<>(immediateTradeService.updateStatus(tradeId, reqDto, member),
			HttpStatus.OK);
	}
}
