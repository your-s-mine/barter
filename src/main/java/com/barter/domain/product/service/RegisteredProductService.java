package com.barter.domain.product.service;

import org.springframework.stereotype.Service;

import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisteredProductService {

	private final RegisteredProductRepository registeredProductRepository;
	private final MemberRepository memberRepository;

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	public void createRegisteredProduct(CreateRegisteredProductReqDto request) {
		Member requestMember = memberRepository.findById(request.getMemberId())
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));

		RegisteredProduct createdProduct = RegisteredProduct.create(request, requestMember);
		registeredProductRepository.save(createdProduct);
	}
}
