package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindRegisteredProductResDto {

	private Long id;
	private String name;
	private String description;
	private String images;
	private LocalDateTime createdAt;

	public static FindRegisteredProductResDto from(RegisteredProduct product) {
		return FindRegisteredProductResDto.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.images(product.getImages())
			.createdAt(product.getCreatedAt())
			.build();
	}
}
