package com.barter.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.repository.SuggestedProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuggestedProductService {

	private final SuggestedProductRepository suggestedProductRepository;
	private final MemberRepository memberRepository;

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	public void createSuggestedProduct(CreateSuggestedProductReqDto request) {
		Member requestMember = memberRepository.findById(request.getMemberId())
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));

		SuggestedProduct createdProduct = SuggestedProduct.create(request, requestMember);
		suggestedProductRepository.save(createdProduct);
	}

	// 인증/인가가 구현되면 요청 회원 정보를 파라미터로 전달받아 요청한 '등록 물품' 등록자가 요청 회원인지 확인하는 로직을 추가 작성할 것 입니다.
	public FindSuggestedProductResDto findSuggestedProduct(Long suggestedProductId) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(suggestedProductId)
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		return FindSuggestedProductResDto.from(foundProduct);
	}

	// 인증/인가 구현되면 요청 회원이 생성한 '등록 물품들만' 조회하도록 수정할 것으로 보입니다.
	public PagedModel<FindSuggestedProductResDto> findSuggestedProducts(Pageable pageable) {
		Page<FindSuggestedProductResDto> foundProducts = suggestedProductRepository.findAll(pageable)
			.map(FindSuggestedProductResDto::from);

		return new PagedModel<>(foundProducts);
	}
}
