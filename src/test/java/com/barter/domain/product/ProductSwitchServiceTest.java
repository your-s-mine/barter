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
import com.barter.domain.product.dto.response.SwitchSuggestedProductResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.service.ProductSwitchService;

@ExtendWith(MockitoExtension.class)
public class ProductSwitchServiceTest {

	@Mock
	private RegisteredProductRepository registeredProductRepository;
	@Mock
	private SuggestedProductRepository suggestedProductRepository;

	@InjectMocks
	private ProductSwitchService productSwitchService;

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
}
