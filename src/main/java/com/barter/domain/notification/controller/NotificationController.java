package com.barter.domain.notification.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public SseEmitter subscribe(
		@PathVariable(name = "memberId") Long memberId
	) {
		// 현재 인증/인가가 구현되지 않아 테스트를 위해 'path parameter' 로 요청 회원 ID 를 전달받고 있습니다.
		// 이 부분은 추후 인증/인가가 구현되면 수정할 계획입니다.

		return notificationService.subscribe(memberId);
	}

	@GetMapping("/activity")
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindNotificationResDto> findActivityNotifications(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
	) {
		// 인증/인가 파트 구현이 끝난다면, 'NOTIFICATIONS' 테이블에서 요청 회원의 활동 알림들을 조회하도록 할 것 같습니다.

		return notificationService.findActivityNotifications(pageable);
	}
}
