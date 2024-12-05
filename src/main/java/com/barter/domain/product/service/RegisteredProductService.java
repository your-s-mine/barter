package com.barter.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
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

	// 인증/인가가 구현되면 요청 회원 정보를 파라미터로 전달받아 요청한 '등록 물품' 등록자가 요청 회원인지 확인하는 로직을 추가 작성할 것 입니다.
	public FindRegisteredProductResDto findRegisteredProduct(Long RegisteredProductId) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(RegisteredProductId)
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		return FindRegisteredProductResDto.from(foundProduct);
	}

	// 인증/인가 구현되면 요청 회원이 생성한 '등록 물품들만' 조회하도록 수정할 것으로 보입니다.
	public PagedModel<FindRegisteredProductResDto> findRegisteredProducts(Pageable pageable) {
		Page<FindRegisteredProductResDto> foundProducts = registeredProductRepository.findAll(pageable)
			.map(FindRegisteredProductResDto::from);

		return new PagedModel<>(foundProducts);
	}
}
