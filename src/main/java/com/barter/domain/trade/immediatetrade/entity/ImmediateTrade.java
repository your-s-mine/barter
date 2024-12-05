package com.barter.domain.trade.immediatetrade.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;

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
@Table(name = "IMMEDIATE_TRADES")
public class ImmediateTrade extends BaseTimeStampEntity {

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

	@Builder
	public ImmediateTrade(String title, String description, RegisteredProduct product, TradeStatus status,
		int viewCount) {
		this.title = title;
		this.description = description;
		this.product = product;
		this.status = status;
		this.viewCount = viewCount;
	}

	public void addViewCount() {
		this.viewCount += 1;
	}

	public void update(UpdateImmediateTradeReqDto reqDto) {
		this.title = reqDto.getTitle();
		this.description = reqDto.getDescription();
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
}
