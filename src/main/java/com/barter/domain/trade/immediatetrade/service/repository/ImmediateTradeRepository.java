package com.barter.domain.trade.immediatetrade.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.immediatetrade.service.entity.ImmediateTrade;

public interface ImmediateTradeRepository extends JpaRepository<ImmediateTrade, Long> {

	List<ImmediateTrade> findByTitleOrDescriptionContaining(String title, String description);
}
