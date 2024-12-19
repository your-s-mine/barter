package com.barter.domain.review.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 활성화
@Table(name = "REVIEWS")
@Builder
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

	// 추가적으로 @Builder와 @AllArgsConstructor를 함께 사용하면 생성자를 빌더에 활용할 수 있습니다.
}
