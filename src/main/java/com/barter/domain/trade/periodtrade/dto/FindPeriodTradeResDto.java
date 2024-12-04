package com.barter.domain.trade.periodtrade.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPeriodTradeResDto {
	private Long periodTradesId;
	private String title;
	private String description;
	private RegisteredProduct product;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	private LocalDateTime endedAt;

	@Builder
	public FindPeriodTradeResDto(Long periodTradesId, String title, String description, RegisteredProduct product,
		LocalDateTime createdAt, LocalDateTime updatedAt, TradeStatus tradeStatus, int viewCount, LocalDateTime endedAt
	) {
		this.periodTradesId = periodTradesId;
		this.title = title;
		this.description = description;
		this.product = product;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
		this.endedAt = endedAt;

	}

	public static FindPeriodTradeResDto from(PeriodTrade periodTrade) {
		return FindPeriodTradeResDto.builder()
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

	public static List<FindPeriodTradeResDto> from(List<PeriodTrade> periodTrades) {
		return periodTrades.stream()
			.map(FindPeriodTradeResDto::from)
			.toList();
	}

}
