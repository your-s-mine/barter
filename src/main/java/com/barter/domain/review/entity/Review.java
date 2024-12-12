package com.barter.domain.review.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자
@Table(name = "REVIEWS")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long reviewerId; // FK 대신 ID 사용
	private Long revieweeId; // FK 대신 ID 사용
	private Long tradeProductId; // FK 대신 ID 사용

	private String content;
	private Double score;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public Review(Long reviewerId, Long revieweeId, Long tradeProductId, String content, Double score, LocalDateTime createdAt) {
		this.reviewerId = reviewerId;
		this.revieweeId = revieweeId;
		this.tradeProductId = tradeProductId;
		this.content = content;
		this.score = score;
		this.createdAt = createdAt;
	}
}
