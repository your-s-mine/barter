package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.response.CreateSuggestedProductResDto;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.dto.response.UpdateSuggestedProductInfoResDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductStatusReqDto;
import com.barter.domain.product.dto.response.UpdateSuggestedProductStatusResDto;
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

	@Test
	@DisplayName("제안 물품 단건 조회 - 성공 테스트")
	void findSuggestedProductTest_Success() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(List.of("test image1", "test image2"))
				.member(Member.builder().id(1L).build())
				.status(SuggestedStatus.PENDING)
				.build()
			)
		);

		//when
		FindSuggestedProductResDto response = suggestedProductService.findSuggestedProduct(
			suggestedProductId, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("test product");
		assertThat(response.getDescription()).isEqualTo("test description");
		assertThat(response.getImages()).containsExactly("test image1", "test image2");
		assertThat(response.getStatus()).isEqualTo(SuggestedStatus.PENDING.name());
	}

	@Test
	@DisplayName("제안 물품 단건 조회 - 조회 제안 물품이 없을 경우 예외 테스트")
	void findSuggestedProductTest_Exception1() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId))
			.thenThrow(new IllegalArgumentException("Suggested product not found"));

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.findSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Suggested product not found");
	}

	@Test
	@DisplayName("제안 물품 단건 조회 - 조회 권한이 없는 경우 예외 테스트")
	void findSuggestedProductTest_Exception2() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(List.of("test image1", "test image2"))
				.member(Member.builder().id(2L).build())
				.status(SuggestedStatus.PENDING)
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.findSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("제안 물품 다건 조회 - 성공 테스트")
	void findSuggestedProductsTest_Success() {
		//given
		Long verifiedMemberId = 1L;
		Pageable pageable = PageRequest.of(
			0, 10, Sort.by(Sort.Direction.DESC, "createdAt")
		);

		SuggestedProduct product1 = SuggestedProduct.builder()
			.id(1L)
			.name("test product1")
			.description("product1 description")
			.images(List.of("product1 image1", "product1 image2"))
			.status(SuggestedStatus.PENDING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		SuggestedProduct product2 = SuggestedProduct.builder()
			.id(2L)
			.name("test product2")
			.description("product2 description")
			.images(List.of("product2 image1"))
			.status(SuggestedStatus.SUGGESTING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		SuggestedProduct product3 = SuggestedProduct.builder()
			.id(3L)
			.name("test product3")
			.description("product3 description")
			.images(List.of("product3 image1", "product3 image2", "product3 image3"))
			.status(SuggestedStatus.ACCEPTED)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		List<SuggestedProduct> products = List.of(product3, product2, product1);
		Page<SuggestedProduct> foundProducts = new PageImpl<>(products, pageable, products.size());
		when(suggestedProductRepository.findAllByMemberId(pageable, verifiedMemberId))
			.thenReturn(foundProducts);

		//when
		PagedModel<FindSuggestedProductResDto> response = suggestedProductService.findSuggestedProducts(
			pageable, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(3);
		assertThat(Objects.requireNonNull(response.getMetadata()).size()).isEqualTo(10);
		assertThat(response.getMetadata().number()).isEqualTo(0);
		assertThat(response.getMetadata().totalElements()).isEqualTo(3);
		assertThat(response.getMetadata().totalPages()).isEqualTo(1);
	}

	@Test
	@DisplayName("제안 물품 정보 수정 - 성공 테스트")
	void updateSuggestedProductInfoTest_Success() {
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
	void updateSuggestedProductInfoTest_Exception1() {
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
	void updateSuggestedProductInfoTest_Exception2() {
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
	void updateSuggestedProductInfoTest_Exception3() {
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
	void updateSuggestedProductInfoTest_Exception4() {
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

	@Test
	@DisplayName("제안 물품 상태 수정 - 성공 테스트")
	void updateSuggestedProductStatusTest_Success() {
		//given
		UpdateSuggestedProductStatusReqDto request = UpdateSuggestedProductStatusReqDto.builder()
			.id(1L)
			.status("SUGGESTING")
			.build();

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		when(suggestedProductRepository.save(any())).thenReturn(
			SuggestedProduct.builder()
				.id(1L)
				.status(SuggestedStatus.SUGGESTING)
				.build()
		);

		//when
		UpdateSuggestedProductStatusResDto response = suggestedProductService.updateSuggestedProductStatus(
			request, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(request.getId());
		assertThat(response.getStatus()).isEqualTo(request.getStatus());
	}

	@Test
	@DisplayName("제안 물품 상태 수정 - 수정 제안 물품이 존재하지 않는 경우 예외 테스트")
	void updateSuggestedProductStatusTest_Exception1() {
		//given
		UpdateSuggestedProductStatusReqDto request = UpdateSuggestedProductStatusReqDto.builder()
			.id(1L)
			.status("SUGGESTING")
			.build();

		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(request.getId())).thenThrow(
			new IllegalArgumentException("Suggested product not found")
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductStatus(request, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Suggested product not found");
	}

	@Test
	@DisplayName("제안 물품 상태 수정 - 수정 권한 예외 테스트")
	void updateSuggestedProductStatusTest_Exception2() {
		//given
		UpdateSuggestedProductStatusReqDto request = UpdateSuggestedProductStatusReqDto.builder()
			.id(1L)
			.status("SUGGESTING")
			.build();

		Long verifiedMemberId = 2L;

		when(suggestedProductRepository.findById(request.getId())).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(1L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.updateSuggestedProductStatus(request, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("제안 물품 삭제 - 성공 테스트")
	void deleteSuggestedProductTest_Success() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		SuggestedProduct testProduct = SuggestedProduct.builder()
			.id(suggestedProductId)
			.status(SuggestedStatus.PENDING)
			.images(List.of("image1", "image2"))
			.member(Member.builder().id(verifiedMemberId).build())
			.build();
		suggestedProductRepository.save(testProduct);

		when(suggestedProductRepository.findById(suggestedProductId))
			.thenReturn(Optional.of(testProduct));

		//when
		suggestedProductService.deleteSuggestedProduct(suggestedProductId, verifiedMemberId);

		//then
		assertThat(suggestedProductRepository.count()).isEqualTo(0);
	}

	@Test
	@DisplayName("제안 물품 삭제 - 대상 제안 물품이 존재하지 않는 경우 예외 테스트")
	void deleteSuggestedProductTest_Exception1() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId))
			.thenThrow(new IllegalArgumentException("Suggested product not found"));

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.deleteSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Suggested product not found");
	}

	@Test
	@DisplayName("제안 물품 삭제 - 수정 권한 예외 테스트")
	void deleteSuggestedProductTest_Exception2() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.status(SuggestedStatus.PENDING)
				.member(Member.builder().id(2L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.deleteSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("제안 물품 삭제 - 삭제 가능 상태 예외 테스트")
	void deleteSuggestedProductTest_Exception3() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(SuggestedProduct.builder()
				.id(1L)
				.status(SuggestedStatus.ACCEPTED)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			suggestedProductService.deleteSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("PENDING 상태인 경우에만 제안 물품을 삭제할 수 있습니다.");
	}
}
