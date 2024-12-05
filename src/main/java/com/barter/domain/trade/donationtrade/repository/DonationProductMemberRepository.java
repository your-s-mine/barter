package com.barter.domain.trade.donationtrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.trade.donationtrade.entity.DonationProductMember;

public interface DonationProductMemberRepository extends JpaRepository<DonationProductMember, Long> {
	boolean existsByMemberIdAndDonationTradeId(Long userId, Long tradeId);
}