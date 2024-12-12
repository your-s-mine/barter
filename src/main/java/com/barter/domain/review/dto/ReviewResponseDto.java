package com.barter.domain.review.dto;

import java.time.LocalDateTime;
import com.barter.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private Long id;
    private Long reviewerId;
    private Long revieweeId;
    private Long tradeProductId;
    private String content;
    private Double score;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        ReviewResponseDto response = new ReviewResponseDto();
        response.id = review.getId();
        response.reviewerId = review.getReviewer().getId();
        response.revieweeId = review.getReviewee().getId();
        response.tradeProductId = review.getTradeProduct().getId();
        response.content = review.getContent();
        response.score = review.getScore();
        response.createdAt = review.getCreatedAt();
        return response;
    }
}
