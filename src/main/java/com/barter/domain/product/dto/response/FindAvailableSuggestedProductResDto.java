package com.barter.domain.product.dto.response;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindAvailableSuggestedProductResDto {

	private Long id;
	private String name;

	public static FindAvailableSuggestedProductResDto from(SuggestedProduct suggestedProduct) {
		return FindAvailableSuggestedProductResDto.builder()
			.id(suggestedProduct.getId())
			.name(suggestedProduct.getName())
			.build();
	}
}
