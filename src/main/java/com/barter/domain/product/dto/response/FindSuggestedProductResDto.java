package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindSuggestedProductResDto {

	private Long id;
	private String name;
	private String description;
	private String images;
	private LocalDateTime createdAt;
	private String status;

	public static FindSuggestedProductResDto from(SuggestedProduct product) {
		return FindSuggestedProductResDto.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.images(product.getImages())
			.createdAt(product.getCreatedAt())
			.status(product.getStatus().name())
			.build();
	}
}
