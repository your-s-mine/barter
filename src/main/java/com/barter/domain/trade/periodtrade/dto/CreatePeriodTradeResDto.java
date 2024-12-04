package com.barter.domain.trade.periodtrade.dto;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePeriodTradeResDto {
	private Long periodTradesId;
	private String title;
	private String description;
	private RegisteredProduct product;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	private LocalDateTime endedAt;

	public static CreatePeriodTradeResDto from(PeriodTrade periodTrade) {
		return CreatePeriodTradeResDto.builder()
			.periodTradesId(periodTrade.getId())
			.title(periodTrade.getTitle())
			.description(periodTrade.getDescription())
			.product(periodTrade.getProduct())
			.createdAt(periodTrade.getCreatedAt())
			.updatedAt(periodTrade.getUpdatedAt())
			.viewCount(periodTrade.getViewCount())
			.endedAt(periodTrade.getEndedAt())
			.build();
	}

}
