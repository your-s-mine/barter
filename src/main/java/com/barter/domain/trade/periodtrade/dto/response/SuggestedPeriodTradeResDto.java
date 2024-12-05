package com.barter.domain.trade.periodtrade.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.SuggestedProduct;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuggestedPeriodTradeResDto {
	private Long periodTradeId;
	private List<SuggestedProduct> suggestedProducts;
	private LocalDateTime createdAt;

	@Builder
	public SuggestedPeriodTradeResDto(Long periodTradeId, List<SuggestedProduct> suggestedProducts) {
		this.periodTradeId = periodTradeId;
		this.suggestedProducts = suggestedProducts;
		this.createdAt = LocalDateTime.now();

	}

	public static SuggestedPeriodTradeResDto from(Long periodTradeId, List<SuggestedProduct> suggestedProductName) {
		return SuggestedPeriodTradeResDto.builder()
			.periodTradeId(periodTradeId)
			.suggestedProducts(suggestedProductName)
			.build();

	}

}
