package com.barter.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.TradeType;

public interface TradeProductRepository extends JpaRepository<TradeProduct, Long> {
	List<TradeProduct> findAllByTradeId(Long tradeId);

	List<TradeProduct> findAllByTradeIdAndTradeType(Long tradeId, TradeType type);

	List<TradeProduct> findByTradeId(Long tradeId);
}
