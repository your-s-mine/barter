package com.barter.domain.trade.enums;

public enum TradeStatus {
	PENDING,
	IN_PROGRESS,
	COMPLETED,
	CLOSED,
	IMMEDIATE,  // 즉시 거래
	PERIOD,     // 기간 거래
	DONATION    // 기부 거래
}