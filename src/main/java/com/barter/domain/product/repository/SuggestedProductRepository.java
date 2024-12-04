package com.barter.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.product.entity.SuggestedProduct;

public interface SuggestedProductRepository extends JpaRepository<SuggestedProduct, Long> {
}
