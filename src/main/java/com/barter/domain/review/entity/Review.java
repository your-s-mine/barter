package com.barter.domain.review.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.barter.domain.member.entity.Member;
import com.barter.domain.product.entity.TradeProduct;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

	@ManyToOne
	private Member reviewer;

	@ManyToOne
	private Member reviewee;

	@ManyToOne
	private TradeProduct tradeProduct;

	private String content;
	private Double score;

	@CreatedDate
	private LocalDateTime createdAt;

	// 필요한 모든 필드를 포함하는 생성자 추가
	public Review(Member reviewer, Member reviewee, TradeProduct tradeProduct, String content, Double score, LocalDateTime createdAt) {
		this.reviewer = reviewer;
		this.reviewee = reviewee;
		this.tradeProduct = tradeProduct;
		this.content = content;
		this.score = score;
		this.createdAt = createdAt;
	}
}
