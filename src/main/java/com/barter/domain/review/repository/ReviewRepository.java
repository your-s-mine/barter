package com.barter.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	// 실행 오류로 주석처리 해두었습니다.
	// boolean existsByReviewerAndTradeProduct(Member reviewer, TradeProduct tradeProduct);

	// 리뷰 대상자 ID로 리뷰 조회
	List<Review> findByRevieweeId(Long revieweeId);
}
