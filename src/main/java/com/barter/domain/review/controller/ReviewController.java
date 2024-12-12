package com.barter.domain.review.controller;

import com.barter.domain.auth.dto.VerifiedMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.barter.domain.review.dto.ReviewRequestDto;
import com.barter.domain.review.dto.ReviewResponseDto;
import com.barter.domain.review.entity.Review;
import com.barter.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto requestDto,
            VerifiedMember verifiedMember) {
        Review review = reviewService.createReview(
                verifiedMember,
                requestDto.getRevieweeId(),
                requestDto.getTradeProductId(),
                requestDto.getContent(),
                requestDto.getScore()
        );
        return ResponseEntity.ok(ReviewResponseDto.from(review));
    }
}
