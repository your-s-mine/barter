package com.barter.domain.trade.donationtrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;

public interface DonationTradeRepository extends JpaRepository<DonationTrade, Long> {
}
