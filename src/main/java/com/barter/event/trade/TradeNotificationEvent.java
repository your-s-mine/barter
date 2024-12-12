package com.barter.event.trade;

import com.barter.domain.product.enums.TradeType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeNotificationEvent {
	private final Long tradeId;
	private final TradeType type;
	private final String productName;

	@Builder
	public TradeNotificationEvent(Long tradeId, TradeType type, String productName) {
		this.tradeId = tradeId;
		this.type = type;
		this.productName = productName;
	}
}
