package com.barter.domain.product.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductStatusReqDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
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

	@GetMapping("/{suggestedProductId}")
	@ResponseStatus(HttpStatus.OK)
	public FindSuggestedProductResDto findSuggestedProduct(
		@PathVariable(name = "suggestedProductId") Long suggestedProductId
	) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 ID 정보를 전달 받지 못하고 있습니다.
		// 구현 이후 해당 메서드의 파라미터로 요청 회원 정보를 전달받아 활용하는 쪽으로 수정할 계획입니다.

		return suggestedProductService.findSuggestedProduct(suggestedProductId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindSuggestedProductResDto> findSuggestedProducts(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
		// 인증/인가 파트 구현이 끝난다면, 'SUGGESTED_PRODUCTS' 테이블에서 요청 회원이 생성한 등록 물품들을 조회하도록 할 것 같습니다.

		return suggestedProductService.findSuggestedProducts(pageable);
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void updateSuggestedProductInfo(@RequestBody @Valid UpdateSuggestedProductInfoReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		suggestedProductService.updateSuggestedProductInfo(request);
	}
	
	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void updateSuggestedProductStatus(@RequestBody @Valid UpdateSuggestedProductStatusReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		suggestedProductService.updateSuggestedProductStatus(request);
	}
}
