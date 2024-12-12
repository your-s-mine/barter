package com.barter.domain.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

// 인증/인가 구현이 완료되면 해당 DTO 는 사용하지 않아 삭제할 생각입니다.
@Getter
public class DeleteNotificationReqDto {

	@NotNull
	private Long notificationId;

	@NotNull
	private Long memberId;
}
