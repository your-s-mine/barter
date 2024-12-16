package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateSuggestedProductResDto {

	private Long suggestedProductId;
	private String name;
	private String description;
	private List<String> images;
	private String status;
	private Long memberId;
	private LocalDateTime createdAt;

	public static CreateSuggestedProductResDto from(SuggestedProduct suggestedProduct) {
		return CreateSuggestedProductResDto.builder()
			.suggestedProductId(suggestedProduct.getId())
			.name(suggestedProduct.getName())
			.description(suggestedProduct.getDescription())
			.images(suggestedProduct.getImages())
			.status(suggestedProduct.getStatus().name())
			.memberId(suggestedProduct.getMember().getId())
			.createdAt(suggestedProduct.getCreatedAt())
			.build();
	}
}
