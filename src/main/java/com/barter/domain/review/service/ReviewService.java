package com.barter.domain.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.review.dto.ReviewResponseDto;
import com.barter.domain.review.entity.Review;
import com.barter.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TradeProductRepository tradeProductRepository;

    /**
     * 리뷰 생성 메서드. 새로운 리뷰를 생성하며, 캐시를 무효화합니다.
     * @param verifiedMember 리뷰 작성자 정보
     * @param revieweeId 리뷰 대상자 ID
     * @param tradeProductId 거래 상품 ID
     * @param content 리뷰 내용
     * @param score 리뷰 점수
     * @return 생성된 리뷰의 DTO
     */
    @Transactional
    @CacheEvict(value = "reviewCache", key = "#verifiedMember.id")
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

    /**
     * 나의 평판(리뷰) 조회 메서드. Redis 캐시에서 데이터를 우선적으로 가져옵니다.
     * @param verifiedMember 현재 인증된 사용자 정보
     * @return 사용자에 대한 리뷰 목록
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "reviewCache", key = "#verifiedMember.id", unless = "#result.isEmpty()")
    public List<ReviewResponseDto> getMyReputationReviews(VerifiedMember verifiedMember) {
        System.out.println("Fetching reviews from the database...");
        List<Review> reviews = reviewRepository.findByRevieweeId(verifiedMember.getId());

        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }
}
