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
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
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

	public PagedModel<FindSuggestedProductResDto> findSuggestedProducts(Pageable pageable, Long verifiedMemberId) {
		Page<FindSuggestedProductResDto> foundProducts = suggestedProductRepository
			.findAllByMemberId(pageable, verifiedMemberId)
			.map(FindSuggestedProductResDto::from);

		return new PagedModel<>(foundProducts);
	}

	@Transactional
	public void updateSuggestedProductInfo(
		UpdateSuggestedProductInfoReqDto request, List<MultipartFile> multipartFiles, Long verifiedMemberId
	) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		foundProduct.checkPermission(verifiedMemberId);
		foundProduct.checkPossibleUpdate();

		List<String> deleteImageNames = request.getDeleteImageNames();
		foundProduct.deleteImages(deleteImageNames);
		ImageCountValidator.checkImageCount(foundProduct.getImages().size(), multipartFiles.size());
		deleteImageNames.forEach(s3Service::deleteFile);

		if (!multipartFiles.isEmpty()) {
			List<String> images = s3Service.uploadFile(multipartFiles);
			foundProduct.updateImages(images);
		}

		foundProduct.updateInfo(request);
		suggestedProductRepository.save(foundProduct);
	}

	@Transactional
	public void updateSuggestedProductStatus(UpdateSuggestedProductStatusReqDto request, Long verifiedMemberId) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		foundProduct.checkPermission(verifiedMemberId);

		foundProduct.updateStatus(request.getStatus());
		suggestedProductRepository.save(foundProduct);
	}

	@Transactional
	public void deleteSuggestedProduct(Long suggestedProductId, Long verifiedMemberId) {
		SuggestedProduct foundProduct = suggestedProductRepository.findById(suggestedProductId)
			.orElseThrow(() -> new IllegalArgumentException("Suggested product not found"));

		foundProduct.checkPermission(verifiedMemberId);
		foundProduct.checkPossibleDelete();

		List<String> savedImages = foundProduct.getImages();
		if (!savedImages.isEmpty()) {
			foundProduct.getImages().forEach(s3Service::deleteFile);
		}
		suggestedProductRepository.delete(foundProduct);
	}
}
