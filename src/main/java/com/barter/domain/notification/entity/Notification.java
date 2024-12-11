package com.barter.domain.notification.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.enums.TradeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTIFICATIONS")
public class Notification extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String message;
	@Enumerated(EnumType.STRING)
	private TradeType tradeType;
	private Long tradeId;     // 연관관계 매핑, DB 연관관계 없는거로 생각했습니다.
	private boolean isRead;
	private Long memberId;    // 연관관계 매핑은 하지 않되, DB 연관관계(FK)는 맺는거로 생각했습니다.

	@Builder
	public Notification(String message, TradeType tradeType, Long tradeId, boolean isRead, Long memberId) {
		this.message = message;
		this.tradeType = tradeType;
		this.tradeId = tradeId;
		this.isRead = isRead;
		this.memberId = memberId;
	}

	public static Notification create(String message, TradeType tradeType, Long tradeId, Long memberId) {
		return Notification.builder()
			.message(message)
			.tradeType(tradeType)
			.tradeId(tradeId)
			.isRead(false)
			.memberId(memberId)
			.build();
	}
}
