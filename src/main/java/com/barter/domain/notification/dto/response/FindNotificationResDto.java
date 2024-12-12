package com.barter.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.notification.entity.Notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindNotificationResDto {

	private Long notificationId;
	private String message;
	private String tradeType;
	private Long tradeId;
	private boolean isRead;
	private LocalDateTime createdAt;

	public static FindNotificationResDto from(Notification notification) {
		return FindNotificationResDto.builder()
			.notificationId(notification.getId())
			.message(notification.getMessage())
			.tradeType(notification.getTradeType().name())
			.tradeId(notification.getTradeId())
			.isRead(notification.isRead())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
