package com.barter.domain.trade.periodtrade.entity;

import java.time.LocalDateTime;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
	@ManyToOne(fetch = FetchType.LAZY)
	private RegisteredProduct registeredProduct;
	@Enumerated(EnumType.STRING)
	private TradeStatus status;
	private int viewCount;
	private LocalDateTime endedAt;

	@Builder
	public PeriodTrade(String title, String description, RegisteredProduct product, TradeStatus status,
		int viewCount, LocalDateTime endedAt) {

		this.title = title;
		this.description = description;
		this.registeredProduct = product;
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
		if (endedAt.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("현재 시간보다 적은 시간 예약은 불가능 합니다.");
		}

		// 추가 : 혹시 최소 마감 시간을 정해두면 그것을 여기에 적용하면 좋을 것 같습니다.
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
			throw new IllegalArgumentException("이미 성사된 기간 거래입니다.");
		}
	}

	public void validateIsClosed() {
		if (this.status.equals(TradeStatus.CLOSED)) {
			throw new IllegalArgumentException("종료된 기간 거래 입니다.");
		}
	}

	public void validateInProgress() {
		if (!this.status.equals(TradeStatus.IN_PROGRESS)) {
			throw new IllegalArgumentException("진행 중인 기간 거래만 수락이 가능합니다.");
		}
	}

	public void validateIsPending() {
		if (this.status.equals(TradeStatus.PENDING)) {
			throw new IllegalArgumentException("아직 시작되지 않은 기간 거래 입니다.");
		}
	}

	public void validateAuthority(Long userId) {
		if (!this.registeredProduct.getMember().getId().equals(userId)) {
			throw new IllegalArgumentException("해당 물품에 대한 수정 권한이 없습니다.");
		}

	}

	public void validateSuggestAuthority(Long userId) {
		if (this.registeredProduct.getMember().getId().equals(userId)) {
			throw new IllegalArgumentException("자신의 교환에 제안 할 수 없습니다.");
		}
	}

	public void updateRegisteredProduct(RegisteredStatus status) {
		this.registeredProduct.updateStatus(status.toString());
	}

	public boolean updatePeriodTradeStatus(TradeStatus status) {

		if (status.equals(TradeStatus.CLOSED)) { // CLOSED : 교환 등록자의 물품이 만료되거나 취소된 경우
			this.status = status;
			this.registeredProduct.updateStatus(RegisteredStatus.PENDING.toString());
			return true;
		}
		if (status.equals(TradeStatus.IN_PROGRESS) && this.status.equals(TradeStatus.PENDING)) {
			this.status = status;
			this.registeredProduct.updateStatus(RegisteredStatus.REGISTERING.toString());

			return true;
		}
		return status.equals(this.status); // 같은 경우는 일단 통과

		/*
		가능한 status 업데이트 목록
		: PENDING -> IN_PROGRESS
		: PENDING -> CLOSED
		: IN_PROGRESS -> CLOSED
		: PENDING -> CLOSED
		 */

	}

	public void updatePeriodTradeStatusCompleted() {
		this.status = TradeStatus.COMPLETED;
	}
}

