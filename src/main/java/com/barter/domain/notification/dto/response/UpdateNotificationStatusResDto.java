package com.barter.domain.notification.dto.response;

import com.barter.domain.notification.entity.Notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateNotificationStatusResDto {

	private Long notificationId;
	private boolean isRead;

	public static UpdateNotificationStatusResDto from(Notification notification) {
		return UpdateNotificationStatusResDto.builder()
			.notificationId(notification.getId())
			.isRead(notification.isRead())
			.build();
	}
}
