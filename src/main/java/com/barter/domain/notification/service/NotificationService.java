package com.barter.domain.notification.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.SseEmitters;
import com.barter.domain.notification.dto.request.UpdateNotificationStatusReqDto;
import com.barter.domain.notification.dto.request.DeleteNotificationReqDto;
import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.respository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final SseEmitters sseEmitters;

	public SseEmitter subscribe(Long memberId) {
		return sseEmitters.saveEmitter(memberId);
	}

	/* 메서드명 거슬림 좀 더 생각해볼 필요가 있음, 사용 예시(추후 삭제 예정)
	     - 아래의 예시를 이벤트가 발생하는 비즈니스 로직에서 호출하면 됩니다.
	     - 물론 해당 메서드를 호출할 서비스에 NotificationService 를 주입해야 합니다.
	public void sendSuggestedProductStatusEvent(Long memberId, Long productId, SuggestedStatus status) {
		Notification createdNotification = Notification.createActivityNotification(
			"제안물품 상태가 " + status + "로 변경되었습니다.",
			TradeType.IMMEDIATE, productId, memberId
		);
		Notification savedNotification = notificationRepository.save(createdNotification);

		// 엔티티 파라미터로 사용할게 아니라 DTO 에 담아 전달할 것(필요한 정보만)
		sseEmitters.sendEvent(memberId, "제안 물품 상태 변경", savedNotification);
	}
	*/

	// 인증/인가 구현되면 요청 회원의 '알림 목록' 만 조회하도록 수정할 것입니다.
	public PagedModel<FindNotificationResDto> findActivityNotifications(Pageable pageable) {
		Page<FindNotificationResDto> foundNotifications = notificationRepository.findAllActivityNotification(pageable)
			.map(FindNotificationResDto::from);

		return new PagedModel<>(foundNotifications);
	}

	// 인증/인가 구현되면 요청 회원의 '키워드 알림 목록' 만 조회하도록 수정할 것 입니다.
	public PagedModel<FindNotificationResDto> findKeywordNotifications(Pageable pageable) {
		Page<FindNotificationResDto> foundNotifications = notificationRepository.findAllKeywordNotification(pageable)
			.map(FindNotificationResDto::from);

		return new PagedModel<>(foundNotifications);
	}

	// 인증/인가 구현되면 RequestBody 가 아닌 파라미터로 전달된 요청 회원 정보로 알림 대상인지 확인할 계획입니다.
	@Transactional
	public void updateNotificationStatus(UpdateNotificationStatusReqDto request) {
		Notification foundNotification = notificationRepository.findById(request.getNotificationId())
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));

		if (!Objects.equals(foundNotification.getMemberId(), request.getMemberId())) {
			throw new IllegalArgumentException("수정 권한이 없습니다.");
		}

		foundNotification.updateStatus();
		notificationRepository.save(foundNotification);
	}

	// 인증/인가 구현 이후에는 DTO 가 아닌 검증 회원 ID 와 알림 ID, 2개의 파라미터를 전달받을 계획입니다.
	@Transactional
	public void deleteNotification(DeleteNotificationReqDto request) {
		Notification foundNotification = notificationRepository.findById(request.getNotificationId())
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));

		if (!Objects.equals(request.getMemberId(), foundNotification.getMemberId())) {
			throw new IllegalArgumentException("삭제 권한이 없습니다.");
		}

		foundNotification.checkPossibleDelete();
		notificationRepository.delete(foundNotification);
	}
}
