package com.barter.domain.review.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.review.entity.Review;
import com.barter.domain.review.repository.ReviewRepository;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.auth.dto.VerifiedMember;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TradeProductRepository tradeProductRepository;

    @Transactional
    public Review createReview(VerifiedMember verifiedMember, Long revieweeId, Long tradeProductId, String content, Double score) {
        Member reviewer = memberRepository.findById(verifiedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        // 리뷰 대상자 가져오기
        Member reviewee = memberRepository.findById(revieweeId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewee not found"));

        // 거래 상품 확인
        TradeProduct tradeProduct = tradeProductRepository.findById(tradeProductId)
                .orElseThrow(() -> new IllegalArgumentException("Trade product not found"));

        // 중복 리뷰 방지
        if (reviewRepository.existsByReviewerAndTradeProduct(reviewer, tradeProduct)) {
            throw new IllegalStateException("Review already exists for this trade product");
        }

        // 점수 유효성 검사
        if (score < 1.0 || score > 5.0) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        // Review 객체 생성
        Review review = new Review(
                reviewer,
                reviewee,
                tradeProduct,
                content,
                score,
                LocalDateTime.now()
        );

        // 저장
        return reviewRepository.save(review);
    }
}
