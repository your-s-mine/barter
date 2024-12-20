package com.barter.domain.search.dto;

import com.barter.domain.trade.enums.TradeStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchTradeResDto {
	private String title;
	private ConvertRegisteredProductDto product;
	private TradeStatus tradeStatus;
	private int viewCount;

	@Builder
	public SearchTradeResDto(String title, ConvertRegisteredProductDto product, TradeStatus tradeStatus,
		int viewCount) {
		this.title = title;
		this.product = product;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
	}
}
