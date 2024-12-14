package com.barter.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.common.s3.S3Service;
import com.barter.domain.member.entity.Member;
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
	@DisplayName("등록 물품 삭제 - 성공 테스트")
	void deleteRegisteredProductTest_Success() {
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
	@DisplayName("등록 물품 삭제 - 대상 등록 물품이 존재하지 않는 경우 예외 테스트")
	void deleteRegisteredProductTest_Exception1() {
		//given
		Long registeredProductId = 1L;
		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(registeredProductId))
			.thenThrow(new IllegalArgumentException("Registered product not found"));

		//when & then
		Assertions.assertThatThrownBy(() ->
				registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Registered product not found");
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
		Assertions.assertThatThrownBy(() ->
				registeredProductService.deleteRegisteredProduct(registeredProductId, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}
}
