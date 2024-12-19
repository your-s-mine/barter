package com.barter.domain.search.dto;

import com.barter.domain.search.service.SearchService;
import com.barter.domain.trade.enums.TradeStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchTradeResDto {
	private String title;
	private SearchService.SimpleProductDto product; // RegisteredProduct에서 SimpleProductDto로 변경
	private TradeStatus tradeStatus;
	private int viewCount;

	@Builder
	public SearchTradeResDto(String title, SearchService.SimpleProductDto product, TradeStatus tradeStatus,
		int viewCount) {
		this.title = title;
		this.product = product;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
	}
}
