package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.dto.response.CreateRegisteredProductResDto;
import com.barter.domain.product.dto.response.FindAvailableRegisteredProductResDto;
import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
import com.barter.domain.product.dto.response.UpdateRegisteredProductInfoResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.service.RegisteredProductService;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

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
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_VALID_IMAGE_COUNT.getMessage());
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
			.thenThrow(new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT));

		//when & then
		assertThatThrownBy(() -> registeredProductService.findRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT.getMessage());
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
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 다건 조회 - 성공 테스트")
	void findRegisteredProductsTest_Success() {
		//given
		Long verifiedMemberId = 1L;
		Pageable pageable = PageRequest.of(
			0, 10, Sort.by(Sort.Direction.DESC, "createdAt")
		);

		RegisteredProduct product1 = RegisteredProduct.builder()
			.id(1L)
			.name("test product1")
			.description("test product1 description")
			.images(List.of("test product1 image1", "test product1 image2"))
			.status(RegisteredStatus.PENDING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		RegisteredProduct product2 = RegisteredProduct.builder()
			.id(2L)
			.name("test product2")
			.description("test product2 description")
			.images(List.of("test product2 image1", "test product2 image2"))
			.status(RegisteredStatus.ACCEPTED)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		RegisteredProduct product3 = RegisteredProduct.builder()
			.id(3L)
			.name("test product3")
			.description("test product3 description")
			.images(List.of("test product3 image1", "test product3 image2"))
			.status(RegisteredStatus.REGISTERING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		List<RegisteredProduct> products = List.of(product3, product2, product1);
		Page<RegisteredProduct> foundProducts = new PageImpl<>(products, pageable, products.size());
		when(registeredProductRepository.findAllByMemberId(pageable, verifiedMemberId))
			.thenReturn(foundProducts);

		//when
		PagedModel<FindRegisteredProductResDto> response = registeredProductService.findRegisteredProducts(
			pageable, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(3);
		assertThat(Objects.requireNonNull(response.getMetadata()).size()).isEqualTo(10);
		assertThat(response.getMetadata().number()).isEqualTo(0);
		assertThat(Objects.requireNonNull(response.getMetadata()).totalElements()).isEqualTo(3);
		assertThat(response.getMetadata().totalPages()).isEqualTo(1);
	}

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
			new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 정보 수정 - 수정 권한 예외 테스트")
	void updateRegisteredProductInfoTest_Exception2() {
		//given
		UpdateRegisteredProductInfoReqDto request = UpdateRegisteredProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId())).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(2L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 정보 수정 - 수정 가능 상태 예외 테스트")
	void updateRegisteredProductInfoTest_Exception3() {
		//given
		UpdateRegisteredProductInfoReqDto request = UpdateRegisteredProductInfoReqDto.builder()
			.id(1L)
			.build();

		List<MultipartFile> multipartFiles = new ArrayList<>();

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId())).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.name("test product")
				.description("test description")
				.images(new ArrayList<>(Arrays.asList("test image1", "test image2")))
				.status(RegisteredStatus.REGISTERING)
				.member(Member.builder().id(verifiedMemberId).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.REGISTERED_PRODUCT_INFO_UPDATE_IMPOSSIBLE.getMessage());
	}

	@Test
	@DisplayName("등록 물품 정보 수정 - 수정 후 이미지 개수 예외 테스트")
	void updateRegisteredProductInfoTest_Exception4() {
		//given
		UpdateRegisteredProductInfoReqDto request = UpdateRegisteredProductInfoReqDto.builder()
			.id(1L)
			.deleteImageNames(new ArrayList<>())
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

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductInfo(request, multipartFiles, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_VALID_IMAGE_COUNT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 삭제 - 성공 테스트1")
	void deleteRegisteredProductTest_Success1() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		RegisteredProduct testProduct = RegisteredProduct.builder()
			.id(registeredProductId)
			.status(RegisteredStatus.PENDING)
			.images(List.of("image1", "image2"))
			.member(Member.builder().id(verifiedMemberId).build())
			.build();
		registeredProductRepository.save(testProduct);

		when(registeredProductRepository.findById(registeredProductId))
			.thenReturn(Optional.of(testProduct));

		//when
		registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId);

		//then
		assertThat(registeredProductRepository.count()).isEqualTo(0L);
	}

	@Test
	@DisplayName("등록 물품 삭제 - 성공 테스트2")
	void deleteRegisteredProductTest_Success2() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		RegisteredProduct testProduct = RegisteredProduct.builder()
			.id(registeredProductId)
			.status(RegisteredStatus.COMPLETED)
			.images(List.of("image1", "image2"))
			.member(Member.builder().id(verifiedMemberId).build())
			.build();
		registeredProductRepository.save(testProduct);

		when(registeredProductRepository.findById(registeredProductId))
			.thenReturn(Optional.of(testProduct));

		//when
		registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId);

		//then
		assertThat(registeredProductRepository.count()).isEqualTo(0L);
	}

	@Test
	@DisplayName("등록 물품 삭제 - 대상 등록 물품이 존재하지 않는 경우 예외 테스트")
	void deleteRegisteredProductTest_Exception1() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId))
			.thenThrow(new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT));

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 삭제 - 수정 권한 예외 테스트")
	void deleteRegisteredProductTest_Exception2() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(2L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 삭제 - 삭제 가능 상태 예외 테스트")
	void deleteRegisteredProductTest_Exception3() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.status(RegisteredStatus.REGISTERING)
				.member(Member.builder().id(1L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_VALID_STATUS_REGISTERED_PRODUCT_DELETE.getMessage());
	}

	@Test
	@DisplayName("사용 가능한 등록 물품 다건 조회 - 성공 테스트")
	void findAvailableRegisteredProductsTest_Success() {
		//given
		Long verifiedMemberId = 1L;

		RegisteredProduct product1 = RegisteredProduct.builder()
			.id(1L)
			.name("test product1")
			.description("test product1 description")
			.images(List.of("test product1 image1", "test product1 image2"))
			.status(RegisteredStatus.PENDING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		RegisteredProduct product2 = RegisteredProduct.builder()
			.id(2L)
			.name("test product2")
			.description("test product2 description")
			.images(List.of("test product2 image1", "test product2 image2"))
			.status(RegisteredStatus.PENDING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		RegisteredProduct product3 = RegisteredProduct.builder()
			.id(3L)
			.name("test product3")
			.description("test product3 description")
			.images(List.of("test product3 image1", "test product3 image2"))
			.status(RegisteredStatus.REGISTERING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();

		when(registeredProductRepository.findAllAvailableRegisteredProduct(verifiedMemberId)).thenReturn(
			List.of(product1, product2)
		);

		//when
		List<FindAvailableRegisteredProductResDto> response = registeredProductService.findAvailableRegisteredProducts(
			verifiedMemberId);

		//then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(2);
		assertThat(response.get(0).getId()).isEqualTo(1L);
		assertThat(response.get(0).getName()).isEqualTo("test product1");
		assertThat(response.get(1).getId()).isEqualTo(2L);
		assertThat(response.get(1).getName()).isEqualTo("test product2");
	}

}
