package com.barter.domain.notification.service;

import static com.barter.domain.notification.enums.EventKind.*;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.SseEmitters;
import com.barter.domain.notification.respository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final SseEmitters sseEmitters;
	private static final long TIMEOUT = 60 * 60 * 1000;

	public SseEmitter subscribe(Long memberId) {
		SseEmitter emitter = sseEmitters.saveEmitter(memberId, new SseEmitter(TIMEOUT));

		emitter.onTimeout(() -> {
			emitter.complete();
			log.info("타임아웃으로 SseEmitter 연결해제, emitterKey={}", memberId);
		});

		emitter.onError(error -> {
			emitter.complete();
			log.info("클라이언트 연결해제로 인해 SseEmitter 연결해제, emitterKey={}", memberId);
		});

		emitter.onCompletion(() -> {
			sseEmitters.deleteEmitter(memberId);
			log.info("연결해제된 SseEmitter 를 Local-Memory 에서 삭제, emitterKey={}", memberId);
		});

		// 더미 이벤트가 없는 상태로 Timeout 발생시 예외가 발생하기에 SseEmitter 발급시 더미 이벤트를 전달합니다.
		sseEmitters.sendEvent(memberId, DEFAULT.getName(), DEFAULT.getMessage());

		return emitter;
	}

	/* 메서드명 거슬림 좀 더 생각해볼 필요가 있음, 사용 예시(추후 삭제 예정)
	     - 아래의 예시를 이벤트가 발생하는 비즈니스 로직에서 호출하면 됩니다.
	     - 물론 해당 메서드를 호출할 서비스에 NotificationService 를 주입해야 합니다.
	public void sendSuggestedProductStatusEvent(Long memberId, Long productId, SuggestedStatus status) {
		Notification createdNotification = Notification.create(
			"제안물품 상태가 " + status + "로 변경되었습니다.",
			TradeType.IMMEDIATE, productId, memberId
		);
		Notification savedNotification = notificationRepository.save(createdNotification);

		// 엔티티 파라미터로 사용할게 아니라 DTO 에 담아 전달할 것(필요한 정보만)
		sseEmitters.sendEvent(memberId, "제안 물품 상태 변경", savedNotification);
	}
	*/
}
