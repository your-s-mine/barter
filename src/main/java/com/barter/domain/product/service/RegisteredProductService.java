package com.barter.domain.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
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
		List<String> images;
		if (multipartFiles != null) {
			images = s3Service.uploadFile(multipartFiles);
		} else {
			images = new ArrayList<>();
		}

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

		if (!request.getDeleteImageNames().isEmpty()) {
			foundProduct.deleteImages(request.getDeleteImageNames());    // 삭제 요청 이미지들 삭제
		}

		// 추가할 신규 이미지들이 있다면 S3 에 저장후 엔티티 images 에 추가
		if (multipartFiles != null) {
			List<String> images = s3Service.uploadFile(multipartFiles);
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

	@Transactional
	public void deleteRegisteredProduct(Long registeredProductId, Long verifiedMemberId) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(registeredProductId)
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		foundProduct.checkPermission(verifiedMemberId);
		foundProduct.checkPossibleDelete();

		List<String> savedImages = foundProduct.getImages();
		if (!savedImages.isEmpty()) {
			foundProduct.getImages().forEach(s3Service::deleteFile);    // 저장된 이미지 전부 삭제
		}
		registeredProductRepository.delete(foundProduct);
	}

}
