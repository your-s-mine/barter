package com.barter.domain.trade.donationtrade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;

import jakarta.persistence.LockModeType;

public interface DonationTradeRepository extends JpaRepository<DonationTrade, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from DonationTrade d where d.id = :tradeId")
	Optional<DonationTrade> findByIdForUpdate(Long tradeId);

	@Query("SELECT dt FROM DonationTrade dt " +
		"JOIN FETCH dt.product p " +
		"WHERE dt.title LIKE %:keyword% OR dt.description LIKE %:keyword%")
	List<DonationTrade> findDonationTradesWithProduct(@Param("keyword") String keyword);
}
