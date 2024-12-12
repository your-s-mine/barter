package com.barter.domain.product.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.dto.request.SwitchRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSwitchService {

	private final RegisteredProductRepository registeredProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void createRegisteredProductFromSuggestedProduct(SwitchRegisteredProductReqDto request) {
		SuggestedProduct suggestedProduct = suggestedProductRepository.findById(request.getSuggestedProductId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		if (!Objects.equals(suggestedProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("권한이 없습니다.");
		}

		suggestedProduct.checkPossibleDelete();

		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.name(suggestedProduct.getName())
			.description(suggestedProduct.getDescription())
			// .images(suggestedProduct.getImages())	제안 물품으로 등록 물품 생성시 수정할 계획입니다.
			.status(RegisteredStatus.PENDING)
			.member(suggestedProduct.getMember())
			.build();

		registeredProductRepository.save(registeredProduct);
		suggestedProductRepository.delete(suggestedProduct);
	}

	@Transactional
	public void createSuggestedProductFromRegisteredProduct(Long registeredProductId, Long verifiedMemberId) {
		RegisteredProduct registeredProduct = registeredProductRepository.findById(registeredProductId)
			.orElseThrow(() -> new RuntimeException("Registered product not found"));

		registeredProduct.checkPermission(verifiedMemberId);
		registeredProduct.checkPossibleDelete();

		SuggestedProduct suggestedProduct = SuggestedProduct.builder()
			.name(registeredProduct.getName())
			.description(registeredProduct.getDescription())
			.images(registeredProduct.getImages())
			.status(SuggestedStatus.PENDING)
			.member(registeredProduct.getMember())
			.build();

		suggestedProductRepository.save(suggestedProduct);
		registeredProductRepository.delete(registeredProduct);
	}
}
