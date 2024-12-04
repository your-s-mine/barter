package com.barter.domain.trade.immediatetrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;

public interface ImmediateTradeRepository extends JpaRepository<ImmediateTrade, Long> {
}
