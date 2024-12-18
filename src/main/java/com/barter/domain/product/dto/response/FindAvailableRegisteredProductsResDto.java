package com.barter.domain.product.dto.response;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindAvailableRegisteredProductsResDto {

	private Long id;
	private String name;

	public static FindAvailableRegisteredProductsResDto from(RegisteredProduct registeredProduct) {
		return FindAvailableRegisteredProductsResDto.builder()
			.id(registeredProduct.getId())
			.name(registeredProduct.getName())
			.build();
	}
}
