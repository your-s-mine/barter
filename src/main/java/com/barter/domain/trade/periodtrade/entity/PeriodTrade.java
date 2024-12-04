package com.barter.domain.trade.periodtrade.entity;

import java.time.LocalDateTime;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PERIOD_TRADES")
public class PeriodTrade extends BaseTimeStampEntity {

	private static final int MAX_AFTER_DAY = 7;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	@ManyToOne
	private RegisteredProduct product;
	@Enumerated(EnumType.STRING)
	private TradeStatus status;
	private int viewCount;
	private LocalDateTime endedAt;

	@Builder
	public PeriodTrade(String title, String description, RegisteredProduct product, TradeStatus status,
		int viewCount, LocalDateTime endedAt) {

		this.title = title;
		this.description = description;
		this.product = product;
		this.status = status;
		this.viewCount = viewCount;
		this.endedAt = endedAt;

	}

	public static PeriodTrade createInitPeriodTrade(String title, String description, RegisteredProduct product,
		LocalDateTime endedAt) {

		return PeriodTrade.builder()
			.title(title)
			.description(description)
			.product(product)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.endedAt(endedAt)
			.build();
	}

	public void validateIsExceededMaxEndDate() {
		if (endedAt.minusDays(MAX_AFTER_DAY).isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("종료일자는 오늘로부터 7일 이내만 가능합니다.");
		}
	}

	public void addViewCount() {
		this.viewCount++;
	}

	public void update(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public void validateIsCompleted() {
		if (this.status.equals(TradeStatus.COMPLETED)) {
			throw new IllegalArgumentException("이미 성사된 기간 거래는 취소 할 수 없습니다.");
		}
	}

	public void validateAuthority(Long userId) {
		if (!this.product.getMember().getId().equals(userId)) {
			throw new IllegalArgumentException("해당 물품에 대한 수정 권한이 없습니다.");
		}

	}
}

