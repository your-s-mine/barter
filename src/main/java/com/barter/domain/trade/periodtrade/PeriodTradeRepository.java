package com.barter.domain.trade.periodtrade;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

public interface PeriodTradeRepository extends JpaRepository<PeriodTrade, Long> {
}
