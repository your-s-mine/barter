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
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.SwitchRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductStatusReqDto;
import com.barter.domain.product.dto.response.CreateRegisteredProductResDto;
import com.barter.domain.product.dto.response.FindAvailableRegisteredProductResDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
import com.barter.domain.product.dto.response.SwitchRegisteredProductResDto;
import com.barter.domain.product.dto.response.UpdateRegisteredProductInfoResDto;
import com.barter.domain.product.dto.response.UpdateRegisteredProductStatusResDto;
import com.barter.domain.product.service.ProductSwitchService;
import com.barter.domain.product.service.RegisteredProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/registered-products")
@RequiredArgsConstructor
public class RegisteredProductController {

	private final RegisteredProductService registeredProductService;
	private final ProductSwitchService productSwitchService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateRegisteredProductResDto createRegisteredProduct(
		@RequestPart(name = "request") @Valid CreateRegisteredProductReqDto request,
		@RequestPart(name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		return registeredProductService.createRegisteredProduct(request, multipartFiles, verifiedMember.getId());
	}

	@GetMapping("/{registeredProductId}")
	@ResponseStatus(HttpStatus.OK)
	public FindRegisteredProductResDto findRegisteredProduct(
		@PathVariable(name = "registeredProductId") Long registeredProductId,
		VerifiedMember verifiedMember
	) {
		return registeredProductService.findRegisteredProduct(registeredProductId, verifiedMember.getId());
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindRegisteredProductResDto> findRegisteredProducts(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
		VerifiedMember verifiedMember
	) {
		return registeredProductService.findRegisteredProducts(pageable, verifiedMember.getId());
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public UpdateRegisteredProductInfoResDto updateRegisteredProductInfo(
		@RequestPart(name = "request") @Valid UpdateRegisteredProductInfoReqDto request,
		@RequestPart(required = false, name = "multipartFiles") List<MultipartFile> multipartFiles,
		VerifiedMember verifiedMember
	) {
		return registeredProductService.updateRegisteredProductInfo(
			request, Objects.requireNonNullElseGet(multipartFiles, ArrayList::new), verifiedMember.getId()
		);
	}

	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public UpdateRegisteredProductStatusResDto updateRegisteredProductStatus(
		@RequestBody @Valid UpdateRegisteredProductStatusReqDto request,
		VerifiedMember verifiedMember
	) {
		return registeredProductService.updateRegisteredProductStatus(request, verifiedMember.getId());
	}

	@DeleteMapping("/{registeredProductId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRegisteredProduct(
		@PathVariable(name = "registeredProductId") Long registeredProductId, VerifiedMember verifiedMember
	) {
		registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMember.getId());
	}

	@PostMapping("/switch")
	@ResponseStatus(HttpStatus.CREATED)
	public SwitchRegisteredProductResDto createRegisteredProductFromSuggestedProduct(
		@RequestBody @Valid SwitchRegisteredProductReqDto request,
		VerifiedMember verifiedMember
	) {
		return productSwitchService.createRegisteredProductFromSuggestedProduct(
			request.getSuggestedProductId(), verifiedMember.getId()
		);
	}

	@GetMapping("/available")
	@ResponseStatus(HttpStatus.OK)
	public List<FindAvailableRegisteredProductResDto> findAvailableRegisteredProducts(VerifiedMember verifiedMember) {
		return registeredProductService.findAvailableRegisteredProducts(verifiedMember.getId());
	}
}
