package com.barter.domain.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.SseEmitters;
import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.dto.response.SendTradeEventResDto;
import com.barter.domain.notification.dto.response.UpdateNotificationStatusResDto;
import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.respository.NotificationRepository;
import com.barter.domain.product.enums.TradeType;

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

	public PagedModel<FindNotificationResDto> findActivityNotifications(Pageable pageable, Long verifiedMemberId) {
		Page<FindNotificationResDto> foundNotifications = notificationRepository
			.findAllActivityNotification(pageable, verifiedMemberId)
			.map(FindNotificationResDto::from);

		return new PagedModel<>(foundNotifications);
	}

	public PagedModel<FindNotificationResDto> findKeywordNotifications(Pageable pageable, Long verifiedMemberId) {
		Page<FindNotificationResDto> foundNotifications = notificationRepository
			.findAllKeywordNotification(pageable, verifiedMemberId)
			.map(FindNotificationResDto::from);

		return new PagedModel<>(foundNotifications);
	}

	@Transactional
	public UpdateNotificationStatusResDto updateNotificationStatus(
		Long notificationId, Long verifiedMemberId
	) {
		Notification foundNotification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));

		foundNotification.checkPermission(verifiedMemberId);

		foundNotification.updateStatus();
		Notification updatedNotification = notificationRepository.save(foundNotification);
		return UpdateNotificationStatusResDto.from(updatedNotification);
	}

	@Transactional
	public void deleteNotification(Long notificationId, Long verifiedMemberId) {
		Notification foundNotification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));

		foundNotification.checkPermission(verifiedMemberId);

		foundNotification.checkPossibleDelete();
		notificationRepository.delete(foundNotification);
	}

	public void saveTradeEvent(
		EventKind eventKind, Long memberId, TradeType tradeType, Long tradeId, String tradeTitle
	) {
		String completedEventMessage = EventKind.completeEventMessage(eventKind, tradeTitle);

		Notification createdNotification = Notification.createActivityNotification(
			completedEventMessage, tradeType, tradeId, memberId
		);
		Notification savedNotification = notificationRepository.save(createdNotification);

		sseEmitters.sendEvent(memberId, eventKind.getEventName(), SendTradeEventResDto.from(savedNotification));
	}
}
