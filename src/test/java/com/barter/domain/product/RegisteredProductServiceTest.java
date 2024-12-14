package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.dto.response.UpdateRegisteredProductInfoResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.service.RegisteredProductService;

@ExtendWith(MockitoExtension.class)
public class RegisteredProductServiceTest {

	@Mock
	private RegisteredProductRepository registeredProductRepository;
	@Mock
	private S3Service s3Service;

	@InjectMocks
	private RegisteredProductService registeredProductService;

	@Test
	@DisplayName("등록 물품 정보 수정 - 성공 테스트")
	void updateRegisteredProductInfoTest_Success() {
		//given
		UpdateRegisteredProductInfoReqDto request = UpdateRegisteredProductInfoReqDto.builder()
			.id(1L)
			.name("update product name")
			.description("update product description")
			.deleteImageNames(List.of("test image1"))
			.build();

		MultipartFile imageFile = new MockMultipartFile("test image", "test".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile);

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId())).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		when(s3Service.uploadFile(multipartFiles)).thenReturn(List.of("new image1, new image2"));

		when(registeredProductRepository.save(any())).thenReturn(
			RegisteredProduct.builder()
				.id(1L)
				.name(request.getName())
				.description(request.getDescription())
				.images(List.of("test image2", "new image1", "new image2"))
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
		);

		//when
		UpdateRegisteredProductInfoResDto response = registeredProductService.updateRegisteredProductInfo(
			request, multipartFiles, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(request.getName());
		assertThat(response.getDescription()).isEqualTo(request.getDescription());
		assertThat(response.getImages().size()).isEqualTo(3);
		assertThat(response.getImages().get(0)).isEqualTo("test image2");
		assertThat(response.getImages().get(1)).isEqualTo("new image1");
		assertThat(response.getImages().get(2)).isEqualTo("new image2");
	}

	@Test
	@DisplayName("등록 물품 정보 수정 - 수정할 등록 물품이 없을 경우 예외 테스트")
	void updateRegisteredProductInfoTest_Exception1() {
		//given
		UpdateRegisteredProductInfoReqDto request = UpdateRegisteredProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId())).thenThrow(
			new IllegalArgumentException("Registered product not found")
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Registered product not found");
	}
}
