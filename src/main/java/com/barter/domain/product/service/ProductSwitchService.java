package com.barter.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.dto.response.SwitchRegisteredProductResDto;
import com.barter.domain.product.dto.response.SwitchSuggestedProductResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSwitchService {

	private final RegisteredProductRepository registeredProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;

	@Transactional
	public SwitchRegisteredProductResDto createRegisteredProductFromSuggestedProduct(
		Long suggestedProductId, Long verifiedMemberId
	) {
		SuggestedProduct suggestedProduct = suggestedProductRepository.findById(suggestedProductId)
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		suggestedProduct.checkPermission(verifiedMemberId);
		suggestedProduct.checkPossibleDelete();

		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.name(suggestedProduct.getName())
			.description(suggestedProduct.getDescription())
			.images(suggestedProduct.getImages())
			.status(RegisteredStatus.PENDING)
			.member(suggestedProduct.getMember())
			.build();

		RegisteredProduct savedProduct = registeredProductRepository.save(registeredProduct);
		suggestedProductRepository.delete(suggestedProduct);
		return SwitchRegisteredProductResDto.from(savedProduct);
	}

	@Transactional
	public SwitchSuggestedProductResDto createSuggestedProductFromRegisteredProduct(
		Long registeredProductId, Long verifiedMemberId
	) {
		RegisteredProduct registeredProduct = registeredProductRepository.findById(registeredProductId)
			.orElseThrow(() -> new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT));

		registeredProduct.checkPermission(verifiedMemberId);
		registeredProduct.checkPossibleDelete();

		SuggestedProduct suggestedProduct = SuggestedProduct.builder()
			.name(registeredProduct.getName())
			.description(registeredProduct.getDescription())
			.images(registeredProduct.getImages())
			.status(SuggestedStatus.PENDING)
			.member(registeredProduct.getMember())
			.build();

		SuggestedProduct savedProduct = suggestedProductRepository.save(suggestedProduct);
		registeredProductRepository.delete(registeredProduct);
		return SwitchSuggestedProductResDto.from(savedProduct);
	}
}
