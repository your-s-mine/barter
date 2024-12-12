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
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.DeleteRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductStatusReqDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisteredProductService {

	private final RegisteredProductRepository registeredProductRepository;
	private final S3Service s3Service;

	public void createRegisteredProduct(
		CreateRegisteredProductReqDto request, List<MultipartFile> multipartFiles, Long verifiedMemberId
	) {
		Member requestMember = Member.builder().id(verifiedMemberId).build();
		List<String> images = s3Service.uploadFile(multipartFiles);

		RegisteredProduct createdProduct = RegisteredProduct.create(request, requestMember, images);
		registeredProductRepository.save(createdProduct);
	}

	public FindRegisteredProductResDto findRegisteredProduct(Long RegisteredProductId, Long verifiedMemberId) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(RegisteredProductId)
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		foundProduct.checkPermission(verifiedMemberId);

		return FindRegisteredProductResDto.from(foundProduct);
	}

	public PagedModel<FindRegisteredProductResDto> findRegisteredProducts(Pageable pageable, Long verifiedMemberId) {
		Page<FindRegisteredProductResDto> foundProducts = registeredProductRepository
			.findAllByMemberId(pageable, verifiedMemberId)
			.map(FindRegisteredProductResDto::from);

		return new PagedModel<>(foundProducts);
	}

	@Transactional
	public void updateRegisteredProductInfo(
		UpdateRegisteredProductInfoReqDto request, List<MultipartFile> multipartFiles, Long verifiedMemberId
	) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		foundProduct.checkPermission(verifiedMemberId);
		foundProduct.checkPossibleUpdate();

		// 이미지를 수정하지 않는 경우
		if (multipartFiles != null) {
			foundProduct.getImages().forEach(s3Service::deleteFile);    // 이전 이미지 전부 삭제
			List<String> images = s3Service.uploadFile(multipartFiles);    // 수정 이미지 전부 저장
			foundProduct.updateImages(images);
		}
		foundProduct.updateInfo(request);
		registeredProductRepository.save(foundProduct);
	}

	@Transactional
	public void updateRegisteredProductStatus(UpdateRegisteredProductStatusReqDto request, Long verifiedMemberId) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		foundProduct.checkPermission(verifiedMemberId);

		foundProduct.updateStatus(request.getStatus());
		registeredProductRepository.save(foundProduct);
	}

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void deleteRegisteredProduct(DeleteRegisteredProductReqDto request) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다.");
		}

		foundProduct.checkPossibleDelete();
		registeredProductRepository.delete(foundProduct);
	}

}
