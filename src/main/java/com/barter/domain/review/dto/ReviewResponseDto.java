package com.barter.domain.review.dto;

import java.time.LocalDateTime;
import com.barter.domain.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long reviewerId;
    private String reviewerNickname;
    private Long revieweeId;
    private String revieweeNickname;
    private Long tradeProductId;
    private String content;
    private Double score;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        ReviewResponseDto response = new ReviewResponseDto();
        response.id = review.getId();
        response.reviewerId = review.getReviewerId();
        response.revieweeId = review.getRevieweeId();
        response.tradeProductId = review.getTradeProductId();
        response.content = review.getContent();
        response.score = review.getScore();
        response.createdAt = review.getCreatedAt();
        return response;
    }
}