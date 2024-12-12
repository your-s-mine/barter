package com.barter.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.barter.domain.review.entity.Review;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.entity.TradeProduct;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerAndTradeProduct(Member reviewer, TradeProduct tradeProduct);

    // 리뷰 대상자 ID로 리뷰 조회
    List<Review> findByRevieweeId(Long revieweeId);
}
