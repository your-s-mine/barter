package com.barter.domain.trade.periodtrade.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

public interface PeriodTradeCustomRepository {
	List<PeriodTrade> paginationCoveringIndex(Pageable pageable);
}
