package com.barter.domain.notification.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.SseEmitters;
import com.barter.domain.notification.dto.PublishMessageDto;
import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.dto.response.SendEventResDto;
import com.barter.domain.notification.dto.response.UpdateNotificationStatusResDto;
import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.respository.NotificationRepository;
import com.barter.domain.product.enums.TradeType;
import com.barter.exception.customexceptions.NotificationException;
import com.barter.exception.enums.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements MessageListener {

	private final NotificationRepository notificationRepository;
	private final SseEmitters sseEmitters;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

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
			.orElseThrow(() -> new NotificationException(ExceptionCode.NOT_FOUND_NOTIFICATION));

		foundNotification.checkPermission(verifiedMemberId);

		foundNotification.updateStatus();
		Notification updatedNotification = notificationRepository.save(foundNotification);
		return UpdateNotificationStatusResDto.from(updatedNotification);
	}

	@Transactional
	public void deleteNotification(Long notificationId, Long verifiedMemberId) {
		Notification foundNotification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new NotificationException(ExceptionCode.NOT_FOUND_NOTIFICATION));

		foundNotification.checkPermission(verifiedMemberId);

		foundNotification.checkPossibleDelete();
		notificationRepository.delete(foundNotification);
	}

	public void saveTradeNotification(
		EventKind eventKind, Long memberId, TradeType tradeType, Long tradeId, String tradeTitle
	) {
		String completedEventMessage = EventKind.completeEventMessage(eventKind, tradeTitle);

		Notification createdNotification = Notification.createActivityNotification(
			completedEventMessage, tradeType, tradeId, memberId
		);
		Notification savedNotification = notificationRepository.save(createdNotification);

		PublishMessageDto publishMessage = PublishMessageDto.from(
			eventKind.getEventName(), SendEventResDto.from(savedNotification)
		);
		publishEvent("activity", publishMessage);
	}

	@Transactional
	public void saveKeywordNotification(
		EventKind eventKind, List<Long> memberIds, TradeType tradeType, Long tradeId
	) {
		String completedEventMessage = eventKind.getEventMessage();

		List<Notification> keywordNotifications = new ArrayList<>();
		Set<PublishMessageDto> keywordMessages = new HashSet<>();

		memberIds.parallelStream().forEach(memberId -> {
			Notification createdNotification = Notification.createKeywordNotification(
				completedEventMessage, tradeType, tradeId, memberId
			);
			synchronized (keywordNotifications) {
				keywordNotifications.add(createdNotification);
			}

			PublishMessageDto publishMessage = PublishMessageDto.from(
				eventKind.getEventName(), SendEventResDto.from(createdNotification)
			);
			synchronized (keywordMessages) {
				keywordMessages.add(publishMessage);
			}
		});

		notificationRepository.bulkInsert(keywordNotifications);
		publishAllEvent("keyword", keywordMessages);
	}

	private void publishEvent(String channel, PublishMessageDto data) {
		try {
			String jsonData = objectMapper.writeValueAsString(data);
			redisTemplate.convertAndSend(channel, jsonData);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private void publishAllEvent(String channel, Set<PublishMessageDto> data) {
		data.parallelStream().forEach(message -> {
			try {
				String jsonData = objectMapper.writeValueAsString(message);
				redisTemplate.convertAndSend(channel, jsonData);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String jsonMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
			PublishMessageDto publishedMessage = objectMapper.readValue(jsonMessage, PublishMessageDto.class);
			sseEmitters.sendEvent(publishedMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
