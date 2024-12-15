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
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.response.UpdateSuggestedProductInfoResDto;
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
	@DisplayName("제안 물품 정보 수정 - 성공 테스트")
	void updateSuggestedProductTest_Success() {
		//given
		UpdateSuggestedProductInfoReqDto request = UpdateSuggestedProductInfoReqDto.builder()
			.id(1L)
			.name("update product name")
			.description("update product description")
			.deleteImageNames(List.of("test image1"))
			.build();

		MultipartFile imageFile = new MockMultipartFile("test image", "test".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile);

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		when(s3Service.uploadFile(multipartFiles)).thenReturn(List.of("new image1", "new image2"));

		when(suggestedProductRepository.save(any())).thenReturn(
			SuggestedProduct.builder()
				.id(1L)
				.name(request.getName())
				.description(request.getDescription())
				.images(List.of("test image2", "new image1", "new image2"))
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
		);

		//when
		UpdateSuggestedProductInfoResDto response = suggestedProductService.updateSuggestedProductInfo(
			request, multipartFiles, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(request.getName());
		assertThat(response.getDescription()).isEqualTo(request.getDescription());
		assertThat(response.getImages().get(0)).isEqualTo("test image2");
		assertThat(response.getImages().get(1)).isEqualTo("new image1");
		assertThat(response.getImages().get(2)).isEqualTo("new image2");
	}

	@Test
	@DisplayName("제안 물품 정보 수정 - 수정할 제안 물품이 없을 경우 예외 테스트")
	void updateSuggestedProductTest_Exception1() {
		//given
		UpdateSuggestedProductInfoReqDto request = UpdateSuggestedProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenThrow(
			new IllegalArgumentException("Suggested product not found")
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Suggested product not found");
	}

	@Test
	@DisplayName("제안 물품 정보 수정 - 수정 권한 예외 테스트")
	void updateSuggestedProductTest_Exception2() {
		//given
		UpdateSuggestedProductInfoReqDto request = UpdateSuggestedProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(2L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("제안 물품 정보 수정 - 수정 가능 상태 예외 테스트")
	void updateSuggestedProductTest_Exception3() {
		//given
		UpdateSuggestedProductInfoReqDto request = UpdateSuggestedProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(SuggestedStatus.ACCEPTED)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("PENDING 상태인 경우에만 제안 물품을 수정할 수 있습니다.");
	}

	@Test
	@DisplayName("제안 물품 정보 수정 - 수정 후 이미지 개수 예외 테스트")
	void updateSuggestedProductTest_Exception4() {
		//given
		UpdateSuggestedProductInfoReqDto request = UpdateSuggestedProductInfoReqDto.builder()
			.id(1L)
			.deleteImageNames(new ArrayList<>())
			.build();

		MultipartFile imageFile = new MockMultipartFile("test image", "test".getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> multipartFiles = List.of(imageFile, imageFile);

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("1 ~ 3개 사이의 이미지를 가져야 합니다.");
	}
}
