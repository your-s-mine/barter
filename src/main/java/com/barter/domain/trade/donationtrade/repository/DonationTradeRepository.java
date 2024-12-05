package com.barter.domain.trade.donationtrade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;

import jakarta.persistence.LockModeType;

public interface DonationTradeRepository extends JpaRepository<DonationTrade, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from DonationTrade d where d.id = :tradeId")
	Optional<DonationTrade> findByIdForUpdate(Long tradeId);
}
