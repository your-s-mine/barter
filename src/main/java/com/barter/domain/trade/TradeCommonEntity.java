package com.barter.domain.trade;

import org.springframework.scheduling.annotation.Async;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.enums.ExceptionCode;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TradeCommonEntity extends BaseTimeStampEntity {

	String title;
	String description;
	@ManyToOne
	RegisteredProduct registeredProduct;
	@Enumerated(EnumType.STRING)
	TradeStatus status;
	int viewCount;
	String address1;
	String address2;
	// longitude = 경도, x
	Double longitude;
	// latitude = 위도, y
	Double latitude;

	public TradeCommonEntity(String title, String description, RegisteredProduct registeredProduct, TradeStatus status,
		int viewCount, String address1, String address2, Double longitude, Double latitude) {
		this.title = title;
		this.description = description;
		this.registeredProduct = registeredProduct;
		this.status = status;
		this.viewCount = viewCount;
		this.address1 = address1;
		this.address2 = address2;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Async
	public void addViewCount() {
		this.viewCount += 1;
	}

	public void update(UpdateImmediateTradeReqDto reqDto) {
		this.title = reqDto.getTitle();
		this.description = reqDto.getDescription();
		this.address1 = reqDto.getAddress1();
		this.address2 = reqDto.getAddress2();
		this.longitude = reqDto.getLongitude();
		this.latitude = reqDto.getLatitude();
	}

	public boolean validateTradeStatus(TradeStatus status) {
		return status.equals(TradeStatus.PENDING);
	}

	public void changeStatusInProgress() {
		this.status = TradeStatus.IN_PROGRESS;
	}

	public void changeStatusPending() {
		this.status = TradeStatus.PENDING;
	}

	public void changeStatusCompleted() {
		this.status = TradeStatus.COMPLETED;
	}

	public void validateAuthority(Long userId) {
		if (!this.registeredProduct.getMember().getId().equals(userId)) {
			throw new AuthException(ExceptionCode.NO_AUTHORITY);
		}
	}

	public void validateIsSelfSuggest(Long userId) {
		if (this.registeredProduct.getMember().getId().equals(userId)) {
			throw new IllegalArgumentException("본인이 등록한 물품에 교환을 제안할 수 없습니다.");
		}
	}

	public boolean isInProgress() {
		return this.status == TradeStatus.IN_PROGRESS;
	}

}
