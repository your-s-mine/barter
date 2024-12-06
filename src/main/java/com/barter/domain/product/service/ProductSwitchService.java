package com.barter.domain.product.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.product.dto.request.SwitchSuggestedProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSwitchService {

	private final RegisteredProductRepository registeredProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;

	//SuggestedProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void createSuggestedProductFromRegisteredProduct(SwitchSuggestedProductReqDto request) {
		RegisteredProduct registeredProduct = registeredProductRepository.findById(request.getRegisteredProductId())
			.orElseThrow(() -> new RuntimeException("Registered product not found"));

		if (!Objects.equals(registeredProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("권한이 없습니다.");
		}

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
