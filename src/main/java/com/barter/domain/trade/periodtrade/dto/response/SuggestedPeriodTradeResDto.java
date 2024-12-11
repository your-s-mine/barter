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
	private List<Long> suggestedProductIds;
	private LocalDateTime createdAt;

	@Builder
	public SuggestedPeriodTradeResDto(Long periodTradeId, List<Long> suggestedProductIds) {
		this.periodTradeId = periodTradeId;
		this.suggestedProductIds = suggestedProductIds;
		this.createdAt = LocalDateTime.now();

	}

	public static SuggestedPeriodTradeResDto from(Long periodTradeId, List<SuggestedProduct> suggestedProductName) {

		List<Long> suggestedProductIds = suggestedProductName.stream()
			.map(SuggestedProduct::getId).toList();

		return SuggestedPeriodTradeResDto.builder()
			.periodTradeId(periodTradeId)
			.suggestedProductIds(suggestedProductIds)
			.build();

	}

}
