package com.barter.domain.product.dto.response;

import java.util.List;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSuggestedProductInfoResDto {

	private String name;
	private String description;
	private List<String> images;

	public static UpdateSuggestedProductInfoResDto from(SuggestedProduct suggestedProduct) {
		return UpdateSuggestedProductInfoResDto.builder()
			.name(suggestedProduct.getName())
			.description(suggestedProduct.getDescription())
			.images(suggestedProduct.getImages())
			.build();
	}
}
