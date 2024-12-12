package com.barter.event.trade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TradeNotificationEvent {
	private final Long tradeId;
	private final String productName;
}
