package com.barter.domain.product.dto.response;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateRegisteredProductStatusResDto {

	private Long id;
	private String status;

	public static UpdateRegisteredProductStatusResDto form(RegisteredProduct registeredProduct) {
		return UpdateRegisteredProductStatusResDto.builder()
			.id(registeredProduct.getId())
			.status(registeredProduct.getStatus().name())
			.build();
	}
}
