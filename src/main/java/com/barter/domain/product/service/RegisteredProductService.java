package com.barter.domain.product.service;

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
import com.barter.domain.product.validator.ImageCountValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisteredProductService {

	private final RegisteredProductRepository registeredProductRepository;
	private final S3Service s3Service;

	public void createRegisteredProduct(
		CreateRegisteredProductReqDto request, List<MultipartFile> multipartFiles, Long verifiedMemberId
	) {
		ImageCountValidator.checkImageCount(multipartFiles.size());

		List<String> images = s3Service.uploadFile(multipartFiles);
		Member requestMember = Member.builder().id(verifiedMemberId).build();

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

		List<String> deleteImageNames = request.getDeleteImageNames();
		foundProduct.deleteImages(deleteImageNames);    // 삭제 요청 이미지 이름(들) 엔티티에서 삭제
		ImageCountValidator.checkImageCount(foundProduct.getImages().size(), multipartFiles.size());
		deleteImageNames.forEach(s3Service::deleteFile);    // 삭제 요청 이미지들 S3 에서 삭제

		// 추가할 신규 이미지들이 있다면
		if (!multipartFiles.isEmpty()) {
			List<String> images = s3Service.uploadFile(multipartFiles);    // S3 에 신규 이미지 저장
			foundProduct.updateImages(images);    // 엔티티에 신규 이미지 이름(들) 추가
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
