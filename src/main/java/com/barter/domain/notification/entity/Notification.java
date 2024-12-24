package com.barter.domain.notification.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.notification.enums.NotificationType;
import com.barter.domain.product.enums.TradeType;
import com.barter.exception.customexceptions.NotificationException;
import com.barter.exception.enums.ExceptionCode;

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
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	private boolean isRead;
	private Long memberId;    // 연관관계 매핑은 하지 않되, DB 연관관계(FK)는 맺는거로 생각했습니다.

	@Builder
	public Notification(
		Long id, String message, TradeType tradeType, Long tradeId,
		NotificationType notificationType, boolean isRead, Long memberId
	) {
		this.id = id;
		this.message = message;
		this.tradeType = tradeType;
		this.tradeId = tradeId;
		this.notificationType = notificationType;
		this.isRead = isRead;
		this.memberId = memberId;
	}

	public static Notification createActivityNotification(
		String message, TradeType tradeType, Long tradeId, Long memberId
	) {
		return Notification.builder()
			.message(message)
			.tradeType(tradeType)
			.tradeId(tradeId)
			.notificationType(NotificationType.ACTIVITY)
			.isRead(false)
			.memberId(memberId)
			.build();
	}

	public static Notification createKeywordNotification(
		String message, TradeType tradeType, Long tradeId, Long memberId
	) {
		return Notification.builder()
			.message(message)
			.tradeType(tradeType)
			.tradeId(tradeId)
			.notificationType(NotificationType.KEYWORD)
			.isRead(false)
			.memberId(memberId)
			.build();
	}

	public void updateStatus() {
		this.isRead = true;
	}

	public void checkPossibleDelete() {
		if (!this.isRead) {
			throw new NotificationException(ExceptionCode.NOT_READ_NOTIFICATION);
		}
	}

	public void checkPermission(Long memberId) {
		if (!this.memberId.equals(memberId)) {
			throw new NotificationException(ExceptionCode.NOT_OWNER_NOTIFICATION);
		}
	}
}
