package com.barter.domain.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.service.SuggestedProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suggested-products")
@RequiredArgsConstructor
public class SuggestedProductController {

	private final SuggestedProductService suggestedProductService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createSuggestedProduct(@RequestBody @Valid CreateSuggestedProductReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함
		suggestedProductService.createSuggestedProduct(request);
	}
}
