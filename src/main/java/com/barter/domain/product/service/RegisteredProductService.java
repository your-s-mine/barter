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

	// 인증/인가가 구현되면 요청 회원 정보를 파라미터로 전달받아 요청한 '등록 물품' 등록자가 요청 회원인지 확인하는 로직을 추가 작성할 것 입니다.
	public FindRegisteredProductResDto findRegisteredProduct(Long RegisteredProductId, Long verifiedMemberId) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(RegisteredProductId)
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		foundProduct.checkPermission(verifiedMemberId);

		return FindRegisteredProductResDto.from(foundProduct);
	}

	// 인증/인가 구현되면 요청 회원이 생성한 '등록 물품들만' 조회하도록 수정할 것으로 보입니다.
	public PagedModel<FindRegisteredProductResDto> findRegisteredProducts(Pageable pageable) {
		Page<FindRegisteredProductResDto> foundProducts = registeredProductRepository.findAll(pageable)
			.map(FindRegisteredProductResDto::from);

		return new PagedModel<>(foundProducts);
	}

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void updateRegisteredProductInfo(UpdateRegisteredProductInfoReqDto request) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다.");
		}

		foundProduct.updateInfo(request);
		registeredProductRepository.save(foundProduct);
	}

	// RegisteredProductController 와 마찬가지로 요청 회원의 정보가 넘어와야 하므로 인증/인가 구현 완료 이후 수정이 필요함
	@Transactional
	public void updateRegisteredProductStatus(UpdateRegisteredProductStatusReqDto request) {
		RegisteredProduct foundProduct = registeredProductRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Registered product not found"));

		if (!Objects.equals(foundProduct.getMember().getId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다.");
		}

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
