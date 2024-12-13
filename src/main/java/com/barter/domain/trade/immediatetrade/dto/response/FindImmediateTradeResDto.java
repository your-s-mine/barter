package com.barter.domain.trade.immediatetrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindImmediateTradeResDto {

	private String title;
	private String description;
	private Long productId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;

	@Builder
	public FindImmediateTradeResDto(String title, String description, RegisteredProduct product,
		LocalDateTime createdAt, LocalDateTime updatedAt, TradeStatus tradeStatus, int viewCount)
	{
		this.title = title;
		this.description = description;
		this.productId = product.getId();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
	}

	public static FindImmediateTradeResDto from(ImmediateTrade immediateTrade) {
		return FindImmediateTradeResDto.builder()
			.title(immediateTrade.getTitle())
			.description(immediateTrade.getDescription())
			.product(immediateTrade.getProduct())
			.createdAt(immediateTrade.getCreatedAt())
			.updatedAt(immediateTrade.getUpdatedAt())
			.tradeStatus(immediateTrade.getStatus())
			.viewCount(immediateTrade.getViewCount())
			.build();
	}
}
