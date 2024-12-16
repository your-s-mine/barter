package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
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
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.response.CreateRegisteredProductResDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
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
	@DisplayName("등록 물품 생성 - 성공 테스트")
	void createRegisteredProductTest_Success() {
		//given
		CreateRegisteredProductReqDto request = CreateRegisteredProductReqDto.builder()
			.name("test product")
			.description("test description")
			.build();

		MultipartFile imageFile = new MockMultipartFile(
			"file", "test.png", "text/plain", "test".getBytes(StandardCharsets.UTF_8)
		);
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile);

		Long verifiedMemberId = 1L;

		when(s3Service.uploadFile(multipartFiles)).thenReturn(List.of("testImage1", "testImage2"));

		when(registeredProductRepository.save(any())).
			thenReturn(RegisteredProduct.builder()
				.id(1L)
				.name(request.getName())
				.description(request.getDescription())
				.images(List.of("testImage1", "testImage2"))
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			);

		//when
		CreateRegisteredProductResDto response = registeredProductService.createRegisteredProduct(
			request, multipartFiles, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("test product");
		assertThat(response.getDescription()).isEqualTo("test description");
		assertThat(response.getImages().size()).isEqualTo(2);
		assertThat(response.getStatus()).isEqualTo(RegisteredStatus.PENDING.name());
		assertThat(response.getMemberId()).isEqualTo(verifiedMemberId);
	}

	@Test
	@DisplayName("등록 물품 생성 - 최대 이미지 개수 예외 테스트")
	void createRegisteredProductTest_Exception1() {
		//given
		CreateRegisteredProductReqDto request = CreateRegisteredProductReqDto.builder()
			.name("test product")
			.description("test description")
			.build();

		MultipartFile imageFile = new MockMultipartFile(
			"file", "test.png", "text/plain", "test".getBytes(StandardCharsets.UTF_8)
		);
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile, imageFile, imageFile);

		Long verifiedMemberId = 1L;

		//when && then
		assertThatThrownBy(
			() -> registeredProductService.createRegisteredProduct(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("1 ~ 3개 사이의 이미지를 가져야 합니다.");
	}

	@Test
	@DisplayName("등록 물품 단건 조회 - 성공 테스트")
	void findRegisteredProductTest_Success() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.name("test product")
				.description("test product description")
				.images(List.of("image1", "image2"))
				.member(Member.builder().id(verifiedMemberId).build())
				.status(RegisteredStatus.PENDING)
				.build()
			)
		);

		//when
		FindRegisteredProductResDto response = registeredProductService.findRegisteredProduct(
			registeredProductId, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("test product");
		assertThat(response.getDescription()).isEqualTo("test product description");
		assertThat(response.getImages()).containsExactly("image1", "image2");
		assertThat(response.getStatus()).isEqualTo(RegisteredStatus.PENDING.name());
	}

	@Test
	@DisplayName("등록 물품 단건 조회 - 조회 등록 물품이 없을 경우 예외 테스트")
	void findRegisteredProductTest_Exception1() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId))
			.thenThrow(new IllegalArgumentException("Registered product not found"));

		//when & then
		assertThatThrownBy(() -> registeredProductService.findRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Registered product not found");
	}

	@Test
	@DisplayName("등록 물품 단건 조회 - 조회 권한이 없는 경우 예외 테스트")
	void findRegisteredProductTest_Exception2() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.name("test product")
				.description("test product description")
				.images(List.of("image1", "image2"))
				.member(Member.builder().id(2L).build())
				.status(RegisteredStatus.PENDING)
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() -> registeredProductService.findRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("권한이 없습니다.");
	}
}
