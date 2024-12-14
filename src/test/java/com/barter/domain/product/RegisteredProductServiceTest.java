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
}
