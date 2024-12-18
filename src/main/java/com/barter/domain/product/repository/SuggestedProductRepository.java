package com.barter.domain.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.product.entity.SuggestedProduct;

public interface SuggestedProductRepository extends JpaRepository<SuggestedProduct, Long> {

	@Query(value = "SELECT sp FROM SuggestedProduct AS sp WHERE sp.member.id = :memberId")
	Page<SuggestedProduct> findAllByMemberId(Pageable pageable, @Param("memberId") Long memberId);

	@Query(value = "SELECT sp FROM SuggestedProduct AS sp WHERE sp.member.id = :memberId AND sp.status = 'PENDING'")
	List<SuggestedProduct> findAllAvailableSuggestedProduct(@Param("memberId") Long memberId);
}
