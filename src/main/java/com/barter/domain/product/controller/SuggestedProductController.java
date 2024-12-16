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
import com.barter.domain.product.dto.request.SwitchSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductStatusReqDto;
import com.barter.domain.product.dto.response.CreateSuggestedProductResDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.dto.response.UpdateSuggestedProductInfoResDto;
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
	public CreateSuggestedProductResDto createSuggestedProduct(
		@RequestPart(name = "request") @Valid CreateSuggestedProductReqDto request,
		@RequestPart(name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		return suggestedProductService.createSuggestedProduct(request, multipartFiles, verifiedMember.getId());
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
	public UpdateSuggestedProductInfoResDto updateSuggestedProductInfo(
		@RequestPart(name = "request") @Valid UpdateSuggestedProductInfoReqDto request,
		@RequestPart(required = false, name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		return suggestedProductService.updateSuggestedProductInfo(
			request, Objects.requireNonNullElseGet(multipartFiles, ArrayList::new), verifiedMember.getId()
		);
	}

	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void updateSuggestedProductStatus(
		@RequestBody @Valid UpdateSuggestedProductStatusReqDto request,
		VerifiedMember verifiedMember
	) {
		suggestedProductService.updateSuggestedProductStatus(request, verifiedMember.getId());
	}

	@DeleteMapping("/{suggestedProductId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSuggestedProduct(
		@PathVariable(name = "suggestedProductId") Long suggestedProductId, VerifiedMember verifiedMember
	) {
		suggestedProductService.deleteSuggestedProduct(suggestedProductId, verifiedMember.getId());
	}

	@PostMapping("/switch")
	@ResponseStatus(HttpStatus.CREATED)
	public void createSuggestedProductFromRegisteredProduct(
		@RequestBody @Valid SwitchSuggestedProductReqDto request,
		VerifiedMember verifiedMember
	) {
		productSwitchService.createSuggestedProductFromRegisteredProduct(
			request.getRegisteredProductId(), verifiedMember.getId()
		);
	}
}
