package com.barter.domain.trade.immediatetrade.dto.response;

import java.util.List;

import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.enums.SuggestedStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindSuggestForImmediateTradeResDto {

	Long suggestedProductId;
	String suggestedProductName;
	String description;
	List<String> images;
	Long memberId;
	SuggestedStatus suggestedProductStatus;

	@Builder
	public FindSuggestForImmediateTradeResDto(Long suggestedProductId, String suggestedProductName, String description,
		List<String> images, Long memberId, SuggestedStatus suggestedProductStatus) {
		this.suggestedProductId = suggestedProductId;
		this.suggestedProductName = suggestedProductName;
		this.description = description;
		this.images = images;
		this.memberId = memberId;
		this.suggestedProductStatus = suggestedProductStatus;
	}

	public static FindSuggestForImmediateTradeResDto from(SuggestedProduct suggestedProduct) {
		return FindSuggestForImmediateTradeResDto.builder()
			.suggestedProductId(suggestedProduct.getId())
			.suggestedProductName(suggestedProduct.getName())
			.description(suggestedProduct.getDescription())
			.images(suggestedProduct.getImages())
			.memberId(suggestedProduct.getMember().getId())
			.suggestedProductStatus(suggestedProduct.getStatus())
			.build();
	}
}
