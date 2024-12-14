package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;

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

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
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
}
