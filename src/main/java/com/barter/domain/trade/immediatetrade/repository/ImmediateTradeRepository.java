package com.barter.domain.trade.immediatetrade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;

public interface ImmediateTradeRepository extends JpaRepository<ImmediateTrade, Long> {


	@Query("SELECT it FROM ImmediateTrade it " +
		"JOIN FETCH it.product p " +
		"WHERE it.title LIKE %:keyword% OR it.description LIKE %:keyword%")
	List<ImmediateTrade> findImmediateTradesWithProduct(@Param("keyword") String keyword);
}
