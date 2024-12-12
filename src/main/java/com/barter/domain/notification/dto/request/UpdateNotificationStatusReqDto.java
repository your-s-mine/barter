package com.barter.domain.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

// 인증/인가를 반영하면 해당 DTO 는 사용하지 않기에 삭제할 계획입니다.
@Getter
public class UpdateNotificationStatusReqDto {

	@NotNull
	private Long notificationId;

	@NotNull
	private Long memberId;
}
