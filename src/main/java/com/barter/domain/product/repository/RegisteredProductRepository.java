package com.barter.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.product.entity.RegisteredProduct;

public interface RegisteredProductRepository extends JpaRepository<RegisteredProduct, Long> {

	@Query(value = "SELECT rp FROM RegisteredProduct AS rp WHERE rp.member.id = :memberId")
	Page<RegisteredProduct> findAllByMemberId(Pageable pageable, @Param("memberId") Long memberId);
}
