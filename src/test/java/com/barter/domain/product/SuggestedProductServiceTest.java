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
}
