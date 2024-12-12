package com.barter.domain.review.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.review.entity.Review;
import com.barter.domain.review.dto.ReviewResponseDto;
import com.barter.domain.review.repository.ReviewRepository;
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
    public ReviewResponseDto createReview(VerifiedMember verifiedMember, Long revieweeId, Long tradeProductId, String content, Double score) {
        // 리뷰 작성자 확인
        memberRepository.findById(verifiedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 작성자를 찾을 수 없습니다."));

        // 리뷰 대상자 확인
        memberRepository.findById(revieweeId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 대상자를 찾을 수 없습니다."));

        // 거래 상품 확인
        tradeProductRepository.findById(tradeProductId)
                .orElseThrow(() -> new IllegalArgumentException("거래 상품을 찾을 수 없습니다."));

        // 점수 유효성 검사
        if (score < 1.0 || score > 5.0) {
            throw new IllegalArgumentException("점수는 1에서 5 사이여야 합니다.");
        }

        // Review 객체 생성 (빌더 패턴 사용)
        Review review = Review.builder()
                .reviewerId(verifiedMember.getId())
                .revieweeId(revieweeId)
                .tradeProductId(tradeProductId)
                .content(content)
                .score(score)
                .createdAt(LocalDateTime.now())
                .build();

        // 저장 및 ReviewResponseDto 반환
        return ReviewResponseDto.from(reviewRepository.save(review));
    }
}