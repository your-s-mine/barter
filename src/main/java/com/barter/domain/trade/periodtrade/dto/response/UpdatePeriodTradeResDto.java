package com.barter.domain.trade.periodtrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
public class UpdatePeriodTradeResDto {
	private Long periodTradesId;
	private String title;
	private String description;
	private Long registeredProductId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	private LocalDateTime endedAt;
	private String address1;
	private String address2;

	@Builder
	public UpdatePeriodTradeResDto(Long periodTradesId, String title, String description, RegisteredProduct product,
		LocalDateTime createdAt, LocalDateTime updatedAt, TradeStatus tradeStatus, int viewCount, LocalDateTime endedAt
		, String address1, String address2) {
		this.periodTradesId = periodTradesId;
		this.title = title;
		this.description = description;
		this.registeredProductId = product.getId();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
		this.endedAt = endedAt;
		this.address1 = address1;
		this.address2 = address2;

	}

	public static UpdatePeriodTradeResDto from(PeriodTrade periodTrade) {
		return UpdatePeriodTradeResDto.builder()
			.periodTradesId(periodTrade.getId())
			.title(periodTrade.getTitle())
			.description(periodTrade.getDescription())
			.product(periodTrade.getRegisteredProduct())
			.createdAt(periodTrade.getCreatedAt())
			.updatedAt(periodTrade.getUpdatedAt())
			.viewCount(periodTrade.getViewCount())
			.tradeStatus(periodTrade.getStatus())
			.endedAt(periodTrade.getEndedAt())
			.address1(periodTrade.getAddress1())
			.address2(periodTrade.getAddress2())
			.build();
	}
}
