package com.barter.domain.product.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.request.DeleteSuggestedProductReqDto;
import com.barter.domain.product.dto.request.SwitchSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductStatusReqDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.service.ProductSwitchService;
import com.barter.domain.product.service.SuggestedProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suggested-products")
@RequiredArgsConstructor
public class SuggestedProductController {

	private final SuggestedProductService suggestedProductService;
	private final ProductSwitchService productSwitchService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createSuggestedProduct(
		@RequestPart(name = "request") @Valid CreateSuggestedProductReqDto request,
		@RequestPart(name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		suggestedProductService.createSuggestedProduct(request, multipartFiles, verifiedMember.getId());
	}

	@GetMapping("/{suggestedProductId}")
	@ResponseStatus(HttpStatus.OK)
	public FindSuggestedProductResDto findSuggestedProduct(
		@PathVariable(name = "suggestedProductId") Long suggestedProductId,
		VerifiedMember verifiedMember
	) {
		return suggestedProductService.findSuggestedProduct(suggestedProductId, verifiedMember.getId());
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindSuggestedProductResDto> findSuggestedProducts(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
		VerifiedMember verifiedMember
	) {
		return suggestedProductService.findSuggestedProducts(pageable, verifiedMember.getId());
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void updateSuggestedProductInfo(
		@RequestPart(name = "request") @Valid UpdateSuggestedProductInfoReqDto request,
		@RequestPart(required = false, name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		suggestedProductService.updateSuggestedProductInfo(
			request, Objects.requireNonNullElseGet(multipartFiles, ArrayList::new), verifiedMember.getId()
		);
	}

	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void updateSuggestedProductStatus(@RequestBody @Valid UpdateSuggestedProductStatusReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		suggestedProductService.updateSuggestedProductStatus(request);
	}

	// 제안 물품을 삭제하는 API 의 경우, 현재 RequestBody 로 요청 파라미터를 전달 받습니다.
	// 하지만 인증/인가 구현이후 RequestBody 가 아닌 Path Parameter 로 대상 제안 물품의 ID 를 전달 받도록 수정할 생각입니다.
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSuggestedProduct(@RequestBody @Valid DeleteSuggestedProductReqDto request) {
		suggestedProductService.deleteSuggestedProduct(request);
	}

	@PostMapping("/switch")
	@ResponseStatus(HttpStatus.CREATED)
	public void createdSuggestedProductFromRegisteredProduct(
		@RequestBody @Valid SwitchSuggestedProductReqDto request
	) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전돨된다는 가정하에 작성하여 추후 수정이 필요함

		productSwitchService.createSuggestedProductFromRegisteredProduct(request);
	}
}
