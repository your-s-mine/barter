package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.response.SwitchRegisteredProductResDto;
import com.barter.domain.product.dto.response.SwitchSuggestedProductResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.service.ProductSwitchService;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class ProductSwitchServiceTest {

	@Mock
	private RegisteredProductRepository registeredProductRepository;
	@Mock
	private SuggestedProductRepository suggestedProductRepository;

	@InjectMocks
	private ProductSwitchService productSwitchService;

	@Test
	@DisplayName("등록 물품 생성(제안물품을 등록물품으로) - 성공 테스트")
	void createRegisteredProductFromSuggestedProductTest_Success() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		SuggestedProduct suggestedProduct = SuggestedProduct.builder()
			.id(suggestedProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(SuggestedStatus.PENDING)
			.member(Member.builder().id(1L).build())
			.build();
		suggestedProductRepository.save(suggestedProduct);

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(suggestedProduct)
		);

		when(registeredProductRepository.save(any())).thenReturn(
			RegisteredProduct.builder()
				.id(1L)
				.name(suggestedProduct.getName())
				.description(suggestedProduct.getDescription())
				.images(suggestedProduct.getImages())
				.status(RegisteredStatus.PENDING)
				.member(suggestedProduct.getMember())
				.build()
		);

		//when
		SwitchRegisteredProductResDto response = productSwitchService.createRegisteredProductFromSuggestedProduct(
			suggestedProductId, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(suggestedProduct.getName());
		assertThat(response.getDescription()).isEqualTo(suggestedProduct.getDescription());
		assertThat(response.getImages()).isEqualTo(suggestedProduct.getImages());
		assertThat(response.getStatus()).isEqualTo(SuggestedStatus.PENDING.name());
		assertThat(response.getMemberId()).isEqualTo(verifiedMemberId);
		assertThat(suggestedProductRepository.count()).isEqualTo(0L);
	}

	@Test
	@DisplayName("등록 물품 생성(제안물품을 등록물품으로) - 대상 제안 물품이 존재하지 않는 경우 예외 테스트")
	void createRegisteredProductFromSuggestedProductTest_Exception1() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		when(suggestedProductRepository.findById(suggestedProductId)).thenThrow(
			new ProductException(ExceptionCode.NOT_FOUND_SUGGESTED_PRODUCT)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createRegisteredProductFromSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_SUGGESTED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 생성(제안물품을 등록물품으로) - 권한 예외 테스트")
	void createRegisteredProductFromSuggestedProductTest_Exception2() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		SuggestedProduct suggestedProduct = SuggestedProduct.builder()
			.id(suggestedProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(SuggestedStatus.PENDING)
			.member(Member.builder().id(2L).build())
			.build();
		suggestedProductRepository.save(suggestedProduct);

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(suggestedProduct)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createRegisteredProductFromSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_SUGGESTED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("등록 물품 생성(제안물품을 등록물품으로) - 삭제 가능 상태 예외 테스트")
	void createRegisteredProductFromSuggestedProductTest_Exception3() {
		//given
		Long suggestedProductId = 1L;
		Long verifiedMemberId = 1L;

		SuggestedProduct suggestedProduct = SuggestedProduct.builder()
			.id(suggestedProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(SuggestedStatus.SUGGESTING)
			.member(Member.builder().id(1L).build())
			.build();
		suggestedProductRepository.save(suggestedProduct);

		when(suggestedProductRepository.findById(suggestedProductId)).thenReturn(
			Optional.of(suggestedProduct)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createRegisteredProductFromSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.SUGGESTED_PRODUCT_INFO_UPDATE_IMPOSSIBLE.getMessage());
	}

	@Test
	@DisplayName("제안 물품 생성(등록물품을 제안물품으로) - 성공 테스트")
	void createSuggestedProductFromRegisteredProductTest_Success() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.id(registeredProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(RegisteredStatus.PENDING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();
		registeredProductRepository.save(registeredProduct);

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(registeredProduct)
		);

		when(suggestedProductRepository.save(any())).thenReturn(
			SuggestedProduct.builder()
				.id(1L)
				.name(registeredProduct.getName())
				.description(registeredProduct.getDescription())
				.images(registeredProduct.getImages())
				.status(SuggestedStatus.PENDING)
				.member(registeredProduct.getMember())
				.build()
		);

		//when
		SwitchSuggestedProductResDto response = productSwitchService.createSuggestedProductFromRegisteredProduct(
			registeredProductId, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(registeredProduct.getName());
		assertThat(response.getDescription()).isEqualTo(registeredProduct.getDescription());
		assertThat(response.getImages()).isEqualTo(registeredProduct.getImages());
		assertThat(response.getStatus()).isEqualTo(registeredProduct.getStatus().name());
		assertThat(response.getMemberId()).isEqualTo(registeredProduct.getMember().getId());
		assertThat(registeredProductRepository.count()).isEqualTo(0L);
	}

	@Test
	@DisplayName("제안 물품 생성(등록물품을 제안물품으로) - 대상 등록 물품이 존재하지 않는 경우 예외 테스트")
	void createSuggestedProductFromRegisteredProductTest_Exception1() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId)).thenThrow(
			new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createSuggestedProductFromRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("제안 물품 생성(등록물품을 제안물품으로) - 권한 예외 테스트")
	void createSuggestedProductFromRegisteredProductTest_Exception2() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.id(registeredProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(RegisteredStatus.PENDING)
			.member(Member.builder().id(2L).build())
			.build();
		registeredProductRepository.save(registeredProduct);

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(registeredProduct)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createSuggestedProductFromRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_REGISTERED_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("제안 물품 생성(등록물품을 제안물품으로) - 삭제 가능 상태 예외 테스트")
	void createSuggestedProductFromRegisteredProductTest_Exception3() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		RegisteredProduct registeredProduct = RegisteredProduct.builder()
			.id(registeredProductId)
			.name("test product")
			.description("test description")
			.images(List.of("test image1", "test image2"))
			.status(RegisteredStatus.REGISTERING)
			.member(Member.builder().id(verifiedMemberId).build())
			.build();
		registeredProductRepository.save(registeredProduct);

		when(registeredProductRepository.findById(registeredProductId)).thenReturn(
			Optional.of(registeredProduct)
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createSuggestedProductFromRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(ProductException.class)
			.hasMessage(ExceptionCode.REGISTERED_PRODUCT_INFO_UPDATE_IMPOSSIBLE.getMessage());
	}
}
