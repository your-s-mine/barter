package com.barter.domain.trade.periodtrade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

public interface PeriodTradeRepository extends JpaRepository<PeriodTrade, Long> {

	List<PeriodTrade> findByTitleOrDescriptionContaining(String title, String description);
}
