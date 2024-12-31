package com.barter.domain.notification.dto.response;

import com.barter.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEventResDto {

	private String message;
	private String tradeType;
	private Long tradeId;
	private String notificationType;
	private Long memberId;

	public static SendEventResDto from(Notification notification) {
		return SendEventResDto.builder()
			.message(notification.getMessage())
			.tradeType(notification.getTradeType().name())
			.tradeId(notification.getTradeId())
			.notificationType(notification.getNotificationType().name())
			.memberId(notification.getMemberId())
			.build();
	}
}
