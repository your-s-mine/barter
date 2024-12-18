package com.barter.domain.trade.periodtrade.dto.response;

import java.util.List;

import com.barter.domain.product.dto.response.FindSuggestedProductResDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPeriodTradeSuggestionResDto {
	private Long memberId;
	private List<FindSuggestedProductResDto> suggestedProducts;

	@Builder
	public FindPeriodTradeSuggestionResDto(Long memberId, List<FindSuggestedProductResDto> suggestedProducts) {
		this.memberId = memberId;
		this.suggestedProducts = suggestedProducts;
	}

	public static FindPeriodTradeSuggestionResDto from(Long memberId,
		List<FindSuggestedProductResDto> suggestedProducts) {

		return FindPeriodTradeSuggestionResDto.builder()
			.memberId(memberId)
			.suggestedProducts(suggestedProducts)
			.build();

	}
}
