package com.barter.domain.trade.periodtrade.entity;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.trade.TradeCommonEntity;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PERIOD_TRADES", indexes = {
	@Index(name = "idx_updated_at", columnList = "updated_at")
})
public class PeriodTrade extends TradeCommonEntity {

	private static final int MAX_AFTER_DAY = 7;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime endedAt;

	@Builder
	public PeriodTrade(Long id, String title, String description, RegisteredProduct registeredProduct,
		TradeStatus status, int viewCount, String address1, String address2, Double longitude, Double latitude,
		LocalDateTime endedAt) {

		super(title, description, registeredProduct, status, viewCount, address1, address2, longitude, latitude);
		this.id = id;
		this.endedAt = endedAt;

	}

	public static PeriodTrade createInitPeriodTrade(String title, String description, RegisteredProduct product,
		String address1, String address2, Double longitude, Double latitude, LocalDateTime endedAt) {

		return PeriodTrade.builder()
			.title(title)
			.description(description)
			.registeredProduct(product)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.address1(address1)
			.address2(address2)
			.longitude(longitude)
			.latitude(latitude)
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
		super.addViewCount();
	}

	public void update(String title, String description, String address1, String address2, Double longitude,
		Double latitude) {
		this.title = title;
		this.description = description;
		this.address1 = address1;
		this.address2 = address2;
		this.longitude = longitude;
		this.latitude = latitude;
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
		if (status.equals(TradeStatus.COMPLETED)) {
			if (!this.status.equals(TradeStatus.IN_PROGRESS)) {
				return false;
			}
			this.status = status;
			this.registeredProduct.updateStatus(RegisteredStatus.COMPLETED.toString());
			return true;
		}

		if (status.equals(TradeStatus.CLOSED)) { // CLOSED : 교환 등록자의 물품이 만료되거나 취소된 경우
			if (this.status.equals(TradeStatus.COMPLETED)) {
				return false;
			}
			this.status = status;
			this.registeredProduct.updateStatus(RegisteredStatus.PENDING.toString());
			return true;
			// 추가적으로 다른 제안 물품은 pending 상태로 변경해야 한다.
		}
		if (status.equals(this.status)) {
			return true;
		}
		return false;

	}

	public void updatePeriodTradeStatusInProgress() {
		this.status = TradeStatus.IN_PROGRESS;
	}
}

