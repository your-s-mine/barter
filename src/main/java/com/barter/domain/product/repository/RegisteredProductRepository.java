package com.barter.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.product.entity.RegisteredProduct;

public interface RegisteredProductRepository extends JpaRepository<RegisteredProduct, Long> {
}
