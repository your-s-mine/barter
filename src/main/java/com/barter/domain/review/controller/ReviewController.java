package com.barter.domain.review.controller;

import com.barter.domain.auth.dto.VerifiedMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.barter.domain.review.dto.ReviewRequestDto;
import com.barter.domain.review.dto.ReviewResponseDto;
import com.barter.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto requestDto,
            VerifiedMember verifiedMember) {
        // 서비스 계층에서 DTO를 반환하도록 변경
        ReviewResponseDto responseDto = reviewService.createReview(
                verifiedMember,
                requestDto.getRevieweeId(),
                requestDto.getTradeProductId(),
                requestDto.getContent(),
                requestDto.getScore()
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/my-reputation")
    public ResponseEntity<List<ReviewResponseDto>> getMyReputation(VerifiedMember verifiedMember) {
        // 나의 평판(리뷰) 조회
        List<ReviewResponseDto> reviews = reviewService.getMyReputationReviews(verifiedMember);
        return ResponseEntity.ok(reviews);
    }
}
