package com.barter.domain.trade.periodtrade.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuggestedPeriodTradeResDto {
	private Long periodTradeId;
	private List<RegisteredProduct> suggestedProducts;
	private LocalDateTime createdAt;

	@Builder
	public SuggestedPeriodTradeResDto(Long periodTradeId, List<RegisteredProduct> suggestedProducts) {
		this.periodTradeId = periodTradeId;
		this.suggestedProducts = suggestedProducts;
		this.createdAt = LocalDateTime.now();

	}

	public static SuggestedPeriodTradeResDto from(Long periodTradeId, List<RegisteredProduct> suggestedProductName) {
		return SuggestedPeriodTradeResDto.builder()
			.periodTradeId(periodTradeId)
			.suggestedProducts(suggestedProductName)
			.build();

	}

}
