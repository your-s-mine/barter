package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindRegisteredProductResDto {

	private Long id;
	private String name;
	private String description;
	private List<String> images;
	private LocalDateTime createdAt;
	private String status;

	public static FindRegisteredProductResDto from(RegisteredProduct product) {
		return FindRegisteredProductResDto.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.images(product.getImages())
			.createdAt(product.getCreatedAt())
			.status(product.getStatus().name())
			.build();
	}
}
