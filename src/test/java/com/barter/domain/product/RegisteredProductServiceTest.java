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
import com.barter.domain.product.dto.request.UpdateRegisteredProductStatusReqDto;
import com.barter.domain.product.dto.response.UpdateRegisteredProductStatusResDto;
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
	@DisplayName("등록 물품 상태 수정 - 성공 테스트")
	void updateRegisteredProductStatusTest_Success() {
		//given
		UpdateRegisteredProductStatusReqDto request = UpdateRegisteredProductStatusReqDto.builder()
			.id(1L)
			.status("REGISTERING")
			.build();

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId())).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(1L).build())
				.build()
			)
		);

		when(registeredProductRepository.save(any())).thenReturn(
			RegisteredProduct.builder()
				.id(1L)
				.status(RegisteredStatus.REGISTERING)
				.build()
		);

		//when
		UpdateRegisteredProductStatusResDto response = registeredProductService.updateRegisteredProductStatus(
			request, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(request.getId());
		assertThat(response.getStatus()).isEqualTo(request.getStatus());
	}

	@Test
	@DisplayName("등록 물품 상태 수정 - 수정 등록 물품이 존재하지 않는 경우 예외 테스트")
	void updateRegisteredProductStatusTest_Exception1() {
		//given
		UpdateRegisteredProductStatusReqDto request = UpdateRegisteredProductStatusReqDto.builder()
			.id(1L)
			.status("REGISTERING")
			.build();

		Long verifiedMemberId = 1L;

		when(registeredProductRepository.findById(request.getId()))
			.thenThrow(new IllegalArgumentException("Registered product not found"));

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductStatus(request, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Registered product not found");
	}

	@Test
	@DisplayName("등록 물품 상태 수정 - 수정 권환 예외 테스트")
	void updateRegisteredProductStatusTest_Exception2() {
		//given
		UpdateRegisteredProductStatusReqDto request = UpdateRegisteredProductStatusReqDto.builder()
			.id(1L)
			.status("REGISTERING")
			.build();

		Long verifiedMemberId = 2L;

		when(registeredProductRepository.findById(request.getId())).thenReturn(
			Optional.of(RegisteredProduct.builder()
				.id(1L)
				.status(RegisteredStatus.PENDING)
				.member(Member.builder().id(1L).build())
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			registeredProductService.updateRegisteredProductStatus(request, verifiedMemberId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("권한이 없습니다.");
	}
}
