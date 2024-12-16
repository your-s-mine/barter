package com.barter.domain.product.dto.response;

import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateRegisteredProductInfoResDto {

	private String name;
	private String description;
	private List<String> images;

	public static UpdateRegisteredProductInfoResDto from(RegisteredProduct registeredProduct) {
		return UpdateRegisteredProductInfoResDto.builder()
			.name(registeredProduct.getName())
			.description(registeredProduct.getDescription())
			.images(registeredProduct.getImages())
			.build();
	}
}
