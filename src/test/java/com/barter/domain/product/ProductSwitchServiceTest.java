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
			new IllegalArgumentException("Suggested product not found")
		);

		//when & then
		assertThatThrownBy(() ->
			productSwitchService.createRegisteredProductFromSuggestedProduct(suggestedProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Suggested product not found");
	}
}
