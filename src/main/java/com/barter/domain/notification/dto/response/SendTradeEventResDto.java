package com.barter.domain.notification.dto.response;

import com.barter.domain.notification.entity.Notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendTradeEventResDto {

	private Long notificationId;
	private String message;
	private String tradeType;
	private Long tradeId;
	private String notificationType;
	private Long memberId;

	public static SendTradeEventResDto from(Notification notification) {
		return SendTradeEventResDto.builder()
			.notificationId(notification.getId())
			.message(notification.getMessage())
			.tradeType(notification.getTradeType().name())
			.tradeId(notification.getTradeId())
			.notificationType(notification.getNotificationType().name())
			.memberId(notification.getMemberId())
			.build();
	}
}
