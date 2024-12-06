package com.barter.domain.product.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.barter.domain.product.dto.request.SwitchRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;

import jakarta.transaction.Transactional;
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
			.images(suggestedProduct.getImages())
			.status(RegisteredStatus.PENDING)
			.member(suggestedProduct.getMember())
			.build();

		registeredProductRepository.save(registeredProduct);
		suggestedProductRepository.delete(suggestedProduct);
	}
}
