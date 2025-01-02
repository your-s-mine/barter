package com.barter.domain.trade.donationtrade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;

public interface DonationTradeRepository extends JpaRepository<DonationTrade, Long> {

	@Query("SELECT dt FROM DonationTrade dt " +
		"JOIN FETCH dt.product p " +
		"WHERE dt.title LIKE %:keyword% OR dt.description LIKE %:keyword%")
	List<DonationTrade> findDonationTradesWithProduct(@Param("keyword") String keyword);
}
