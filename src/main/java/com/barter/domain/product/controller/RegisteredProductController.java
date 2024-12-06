package com.barter.domain.product.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.DeleteRegisteredProductReqDto;
import com.barter.domain.product.dto.request.SwitchRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductStatusReqDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
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
	public void createRegisteredProduct(@RequestBody @Valid CreateRegisteredProductReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함
		registeredProductService.createRegisteredProduct(request);
	}

	@GetMapping("/{registeredProductId}")
	@ResponseStatus(HttpStatus.OK)
	public FindRegisteredProductResDto findRegisteredProduct(
		@PathVariable(name = "registeredProductId") Long registeredProductId
	) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 ID 정보를 전달 받지 못하고 있습니다.
		// 구현 이후 해당 메서드의 파라미터로 요청 회원 정보를 전달받아 활용하는 쪽으로 수정할 계획입니다.

		return registeredProductService.findRegisteredProduct(registeredProductId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindRegisteredProductResDto> findRegisteredProducts(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
		// 인증/인가 파트 구현이 끝난다면, 'REGISTERED_PRODUCTS' 테이블에서 요청 회원이 생성한 등록 물품들을 조회하도록 할 것 같습니다.

		return registeredProductService.findRegisteredProducts(pageable);
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void updateRegisteredProductInfo(@RequestBody @Valid UpdateRegisteredProductInfoReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		registeredProductService.updateRegisteredProductInfo(request);
	}

	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void updateRegisteredProductStatus(@RequestBody @Valid UpdateRegisteredProductStatusReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		registeredProductService.updateRegisteredProductStatus(request);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRegisteredProduct(@RequestBody @Valid DeleteRegisteredProductReqDto request) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		registeredProductService.deleteRegisteredProduct(request);
	}

	@PostMapping("/switch")
	@ResponseStatus(HttpStatus.CREATED)
	public void createRegisteredProductFromSuggestedProduct(
		@RequestBody @Valid SwitchRegisteredProductReqDto request
	) {
		// 현재 인증/인가 파트의 구현이 완료되지 않아 요청 회원의 정보가 전달된다는 가정하에 작성하여 추후 수정이 필요함

		productSwitchService.createRegisteredProductFromSuggestedProduct(request);
	}
}
