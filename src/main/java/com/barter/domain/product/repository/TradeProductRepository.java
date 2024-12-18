package com.barter.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;

public interface TradeProductRepository extends JpaRepository<TradeProduct, Long> {
	List<TradeProduct> findAllByTradeId(Long tradeId);

	List<TradeProduct> findAllByTradeIdAndTradeType(Long tradeId, TradeType type);

	@Query("SELECT tp FROM TradeProduct tp " +
		"JOIN FETCH tp.suggestedProduct sp " +
		"WHERE tp.tradeType = :tradeType AND tp.tradeId = :tradeId")
	List<TradeProduct> findTradeProductsWithSuggestedProductByPeriodTradeId(
		@Param("tradeType") TradeType tradeType,
		@Param("tradeId") Long tradeId);

	@Query("SELECT tp FROM TradeProduct tp " +
		"JOIN FETCH tp.suggestedProduct sp " +
		"WHERE tp.tradeType = :tradeType AND tp.tradeId = :tradeId AND sp.status != :suggestedStatus")
	List<TradeProduct> findTradeProductsByTradeTypeAndTradeIdAndNotSuggestedStatus(
		@Param("tradeType") TradeType tradeType,
		@Param("tradeId") Long tradeId,
		@Param("suggestedStatus") SuggestedStatus suggestedStatus);

	@Query("SELECT tp FROM TradeProduct tp " +
		"JOIN FETCH tp.suggestedProduct sp " +
		"WHERE tp.tradeType = :tradeType AND tp.tradeId = :tradeId AND sp.status = :suggestedStatus")
	List<TradeProduct> findTradeProductsByTradeTypeAndTradeIdAndSuggestedStatus(
		@Param("tradeType") TradeType tradeType,
		@Param("tradeId") Long tradeId,
		@Param("suggestedStatus") SuggestedStatus suggestedStatus);

}
