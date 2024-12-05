package com.barter.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.product.entity.TradeProduct;

public interface TradeProductRepository extends JpaRepository<TradeProduct, Long> {
}
