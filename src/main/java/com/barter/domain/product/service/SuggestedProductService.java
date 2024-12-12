package com.barter.domain.product.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.request.DeleteSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductStatusReqDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.validator.ImageCountValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuggestedProductService {

	private final SuggestedProductRepository suggestedProductRepository;
	private final S3Service s3Service;

	public void createSuggestedProduct(
		CreateSuggestedProductReqDto request, List<MultipartFile> multipartFiles, Long verifiedMemberId
	) {
		ImageCountValidator.checkImageCount(multipartFiles.size());

		List<String> images = s3Service.uploadFile(multipartFiles);
		Member requestMember = Member.builder().id(verifiedMemberId).build();

		SuggestedProduct createdProduct = SuggestedProduct.create(request, requestMember, images);
		suggestedProductRepository.save(createdProduct);
	}

	public FindSuggestedProductResDto findSuggestedProduct(Long suggestedProductId, Long verifiedMemberId) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(suggestedProductId)
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		foundProduct.checkPermission(verifiedMemberId);

		return FindSuggestedProductResDto.from(foundProduct);
	}

	// 인증/인가 구현되면 요청 회원이 생성한 '등록 물품들만' 조회하도록 수정할 것으로 보입니다.
	public PagedModel<FindSuggestedProductResDto> findSuggestedProducts(Pageable pageable) {
		Page<FindSuggestedProductResDto> foundProducts = suggestedProductRepository.findAll(pageable)
			.map(FindSuggestedProductResDto::from);

		return new PagedModel<>(foundProducts);
	}

	// SuggestedProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void updateSuggestedProductInfo(UpdateSuggestedProductInfoReqDto request) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다");
		}

		foundProduct.updateInfo(request);
		suggestedProductRepository.save(foundProduct);
	}

	// SuggestedProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void updateSuggestedProductStatus(UpdateSuggestedProductStatusReqDto request) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다.");
		}

		foundProduct.updateStatus(request.getStatus());
		suggestedProductRepository.save(foundProduct);
	}

	// SuggestedProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void deleteSuggestedProduct(DeleteSuggestedProductReqDto request) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("삭제 권한이 없습니다.");
		}

		foundProduct.checkPossibleDelete();
		suggestedProductRepository.delete(foundProduct);
	}
}
