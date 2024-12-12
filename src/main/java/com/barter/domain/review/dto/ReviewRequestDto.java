package com.barter.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewRequestDto {
    private Long reviewerId;
    private Long revieweeId;
    private Long tradeProductId;
    private String content;
    private Double score;
}
