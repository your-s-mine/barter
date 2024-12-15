package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
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
}
