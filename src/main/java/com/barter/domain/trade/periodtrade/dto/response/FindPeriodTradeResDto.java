package com.barter.domain.trade.periodtrade.dto.response;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindPeriodTradeResDto {
	private Long periodTradesId;
	private String title;
	private String description;
	private Long registeredProductId;
	private String createdAt;
	private String updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	private String endedAt;
	private String address1;
	private String address2;

	@Builder
	public FindPeriodTradeResDto(Long periodTradesId, String title, String description, RegisteredProduct product,
		String createdAt, String updatedAt, TradeStatus tradeStatus, int viewCount, String endedAt
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

	public static FindPeriodTradeResDto from(PeriodTrade periodTrade) {
		return FindPeriodTradeResDto.builder()
			.periodTradesId(periodTrade.getId())
			.title(periodTrade.getTitle())
			.description(periodTrade.getDescription())
			.product(periodTrade.getRegisteredProduct())
			.createdAt(periodTrade.getCreatedAt().toString())
			.updatedAt(periodTrade.getUpdatedAt().toString())
			.tradeStatus(periodTrade.getStatus())
			.viewCount(periodTrade.getViewCount())
			.endedAt(periodTrade.getEndedAt().toString())
			.address1(periodTrade.getAddress1())
			.address2(periodTrade.getAddress2())
			.build();
	}

}
