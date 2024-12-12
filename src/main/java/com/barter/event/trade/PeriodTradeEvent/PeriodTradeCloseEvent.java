package com.barter.event.trade.PeriodTradeEvent;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.Getter;

@Getter
public class PeriodTradeCloseEvent {
	private final PeriodTrade periodTrade;

	public PeriodTradeCloseEvent(PeriodTrade periodTrade) {
		this.periodTrade = periodTrade;
	}

}
