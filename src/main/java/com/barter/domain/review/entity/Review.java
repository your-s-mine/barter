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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
