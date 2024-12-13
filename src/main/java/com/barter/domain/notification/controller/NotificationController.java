package com.barter.domain.notification.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.notification.dto.request.DeleteNotificationReqDto;
import com.barter.domain.notification.dto.request.UpdateNotificationStatusReqDto;
import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public SseEmitter subscribe(VerifiedMember verifiedMember) {
		return notificationService.subscribe(verifiedMember.getId());
	}

	@GetMapping("/activity")
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindNotificationResDto> findActivityNotifications(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
	) {
		// 인증/인가 파트 구현이 끝난다면, 'NOTIFICATIONS' 테이블에서 요청 회원의 활동 알림들을 조회하도록 할 것 같습니다.

		return notificationService.findActivityNotifications(pageable);
	}

	@GetMapping("/keyword")
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<FindNotificationResDto> findKeywordNotifications(
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
	) {
		// 인증/인가 파트 구현이 끝난다면, 'NOTIFICATIONS' 테이블에서 요청 회원의 키워드 알림들을 조회하도록 할 것 같습니다.

		return notificationService.findKeywordNotifications(pageable);
	}

	@PatchMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void updateNotificationStatus(@RequestBody @Valid UpdateNotificationStatusReqDto request) {
		// 인증/인가 파트 구현이 끝난다면, RequestBody 가 아닌 HttpRequestServlet 을 통해 검증 회원 정보를 전달 받을 것 같습니다.
		// RequestBody 의 'notificationId' 는 path parameter 로 전달받을 생각입니다.

		notificationService.updateNotificationStatus(request);
	}

	// 인증/인가 적용시 RequestBody 를 전달 받지 않고 HttpServletRequest 에서 검증 회원정보를 전달 받을 생각입니다.
	// 알림 ID 의 경우 path parameter 를 통해 전달 받을 생각입니다.
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteNotification(@RequestBody @Valid DeleteNotificationReqDto request) {
		notificationService.deleteNotification(request);
	}
}
