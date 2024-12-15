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
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
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
}
