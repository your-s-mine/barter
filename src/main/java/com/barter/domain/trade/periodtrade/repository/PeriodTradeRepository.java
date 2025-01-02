package com.barter.domain.trade.periodtrade.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

public interface PeriodTradeRepository extends JpaRepository<PeriodTrade, Long> {

	@Query("SELECT pt FROM PeriodTrade pt " +
		"JOIN FETCH pt.registeredProduct p " +
		"WHERE pt.title LIKE %:keyword% OR pt.description LIKE %:keyword% AND pt.address1 LIKE %:address1% "
	)
	List<PeriodTrade> findPeriodTradesWithProduct(@Param("keyword") String keyword, String address1);

	@Query("SELECT pt FROM PeriodTrade pt ORDER BY pt.updatedAt DESC")
	Slice<PeriodTrade> findPeriodTradeByUpdatedAt(Pageable pageable);

	@Modifying
	@Query("UPDATE PeriodTrade pt SET pt.viewCount = pt.viewCount + :viewCount WHERE pt.id = :periodTradeId")
	void updateViewCount(@Param("periodTradeId") Long periodTradeId, @Param("viewCount") Long viewCount);

}
