package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.response.CreateSuggestedProductResDto;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.service.SuggestedProductService;

@ExtendWith(MockitoExtension.class)
public class SuggestedProductServiceTest {

	@Mock
	private SuggestedProductRepository suggestedProductRepository;
	@Mock
	private S3Service s3Service;

	@InjectMocks
	private SuggestedProductService suggestedProductService;

	@Test
	@DisplayName("제안 물품 생성 - 성공 테스트")
	void createSuggestedProductTest_Success() {
		//given
		CreateSuggestedProductReqDto request = CreateSuggestedProductReqDto.builder()
			.name("test product")
			.description("test description")
			.build();

		MultipartFile imageFile = new MockMultipartFile(
			"file", "test.png", "test/plain", "test".getBytes(StandardCharsets.UTF_8)
		);
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile);

		Long verifiedMemberId = 1L;

		when(s3Service.uploadFile(multipartFiles)).thenReturn(List.of("testImage1", "testImage2"));

		when(suggestedProductRepository.save(any())).thenReturn(
			SuggestedProduct.builder()
				.id(1L)
				.name(request.getName())
				.description(request.getDescription())
				.images(List.of("testImage1", "testImage2"))
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
		);

		//when
		CreateSuggestedProductResDto response = suggestedProductService.createSuggestedProduct(
			request, multipartFiles, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(request.getName());
		assertThat(response.getDescription()).isEqualTo(request.getDescription());
		assertThat(response.getImages()).hasSize(2);
		assertThat(response.getStatus()).isEqualTo(SuggestedStatus.PENDING.name());
		assertThat(response.getMemberId()).isEqualTo(verifiedMemberId);
	}

	@Test
	@DisplayName("제안 물품 생성 - 최대 이미지 개수 예외 테스트")
	void createSuggestedProductTest_Exception1() {
		//given
		CreateSuggestedProductReqDto request = CreateSuggestedProductReqDto.builder()
			.name("test product")
			.description("test description")
			.build();

		MultipartFile imageFile = new MockMultipartFile(
			"file", "test.png", "test/plain", "test".getBytes(StandardCharsets.UTF_8)
		);
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile, imageFile, imageFile);

		Long verifiedMemberId = 1L;

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.createSuggestedProduct(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("1 ~ 3개 사이의 이미지를 가져야 합니다.");
	}
}
