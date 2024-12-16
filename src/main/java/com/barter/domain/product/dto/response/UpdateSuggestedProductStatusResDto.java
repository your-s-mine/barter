package com.barter.domain.product.dto.response;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSuggestedProductStatusResDto {

	private Long id;
	private String status;

	public static UpdateSuggestedProductStatusResDto from(SuggestedProduct suggestedProduct) {
		return UpdateSuggestedProductStatusResDto.builder()
			.id(suggestedProduct.getId())
			.status(suggestedProduct.getStatus().name())
			.build();
	}
}
