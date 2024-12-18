package com.barter.domain.product.dto.response;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindAvailableRegisteredProductResDto {

	private Long id;
	private String name;

	public static FindAvailableRegisteredProductResDto from(RegisteredProduct registeredProduct) {
		return FindAvailableRegisteredProductResDto.builder()
			.id(registeredProduct.getId())
			.name(registeredProduct.getName())
			.build();
	}
}
