package com.barter.domain.trade.periodtrade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

public interface PeriodTradeRepository extends JpaRepository<PeriodTrade, Long> {

	@Query("SELECT pt FROM PeriodTrade pt " +
		"JOIN FETCH pt.registeredProduct p " +
		"WHERE pt.title LIKE %:keyword% OR pt.description LIKE %:keyword% AND pt.address1 LIKE %:address1% "
	)
	List<PeriodTrade> findPeriodTradesWithProduct(@Param("keyword") String keyword, String address1);
}
