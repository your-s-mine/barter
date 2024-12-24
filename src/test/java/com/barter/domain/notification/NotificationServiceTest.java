package com.barter.domain.notification;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.dto.response.UpdateNotificationStatusResDto;
import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.enums.NotificationType;
import com.barter.domain.notification.respository.NotificationRepository;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.enums.TradeType;
import com.barter.exception.customexceptions.NotificationException;
import com.barter.exception.enums.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	@Mock
	private NotificationRepository notificationRepository;
	@Mock
	private SseEmitters sseEmitters;

	@InjectMocks
	private NotificationService notificationService;

	@Test
	@DisplayName("알림 구독 - 성공 테스트")
	void subscribeTest_Success() {
		//given
		Long verifiedMemberId = 1L;

		SseEmitter newEmitter = new SseEmitter();
		when(sseEmitters.saveEmitter(verifiedMemberId))
			.thenReturn(newEmitter);

		//when
		SseEmitter savedEmitter = notificationService.subscribe(verifiedMemberId);

		//then
		assertThat(savedEmitter).isNotNull();
		assertThat(savedEmitter).isEqualTo(newEmitter);
	}

	@Test
	@DisplayName("활동 알림 다건 조회 - 성공 테스트")
	void findActivityNotificationTest_Success() {
		//given
		Long verifiedMemberId = 1L;
		Pageable pageable = PageRequest.of(
			0, 10, Sort.by(Sort.Direction.DESC, "createdAt")
		);

		Notification notification1 = Notification.builder()
			.id(1L)
			.message("test notification1")
			.tradeType(TradeType.IMMEDIATE)
			.tradeId(1L)
			.notificationType(NotificationType.ACTIVITY)
			.isRead(false)
			.memberId(1L)
			.build();

		Notification notification2 = Notification.builder()
			.id(2L)
			.message("test notification2")
			.tradeType(TradeType.PERIOD)
			.tradeId(2L)
			.notificationType(NotificationType.ACTIVITY)
			.isRead(false)
			.memberId(1L)
			.build();

		Notification notification3 = Notification.builder()
			.id(3L)
			.message("test notification3")
			.tradeType(TradeType.IMMEDIATE)
			.tradeId(3L)
			.notificationType(NotificationType.ACTIVITY)
			.isRead(false)
			.memberId(1L)
			.build();

		List<Notification> notifications = List.of(notification3, notification2, notification1);
		Page<Notification> foundNotifications = new PageImpl<>(notifications, pageable, notifications.size());
		when(notificationRepository.findAllActivityNotification(pageable, verifiedMemberId))
			.thenReturn(foundNotifications);

		//when
		PagedModel<FindNotificationResDto> response = notificationService.findActivityNotifications(
			pageable, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(3);
		assertThat(Objects.requireNonNull(response.getMetadata()).size()).isEqualTo(10);
		assertThat(response.getMetadata().number()).isEqualTo(0);
		assertThat(response.getMetadata().totalElements()).isEqualTo(3);
		assertThat(response.getMetadata().totalPages()).isEqualTo(1);
	}

	@Test
	@DisplayName("키워드 알림 다건 조회 - 성공 테스트")
	void findKeywordNotificationsTest_Success() {
		//given
		Long verifiedMemberId = 1L;
		Pageable pageable = PageRequest.of(
			0, 10, Sort.by(Sort.Direction.DESC, "createdAt")
		);
		Notification notification1 = Notification.builder()
			.id(1L)
			.message("test notification1")
			.tradeType(TradeType.DONATION)
			.tradeId(10L)
			.notificationType(NotificationType.KEYWORD)
			.isRead(false)
			.memberId(1L)
			.build();

		Notification notification2 = Notification.builder()
			.id(2L)
			.message("test notification2")
			.tradeType(TradeType.PERIOD)
			.tradeId(14L)
			.notificationType(NotificationType.KEYWORD)
			.isRead(false)
			.memberId(1L)
			.build();

		Notification notification3 = Notification.builder()
			.id(3L)
			.message("test notification3")
			.tradeType(TradeType.IMMEDIATE)
			.tradeId(25L)
			.notificationType(NotificationType.KEYWORD)
			.isRead(false)
			.memberId(1L)
			.build();

		List<Notification> notifications = List.of(notification3, notification2, notification1);
		Page<Notification> foundNotifications = new PageImpl<>(notifications, pageable, notifications.size());
		when(notificationRepository.findAllKeywordNotification(pageable, verifiedMemberId))
			.thenReturn(foundNotifications);

		//when
		PagedModel<FindNotificationResDto> response = notificationService.findKeywordNotifications(
			pageable, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(3);
		assertThat(Objects.requireNonNull(response.getMetadata()).size()).isEqualTo(10);
		assertThat(response.getMetadata().number()).isEqualTo(0);
		assertThat(response.getMetadata().totalElements()).isEqualTo(3);
		assertThat(response.getMetadata().totalPages()).isEqualTo(1);
	}

	@Test
	@DisplayName("알림 상태 수정 - 정상 테스트")
	void updateNotificationTest_Success() {
		//given
		Long notificationId = 1L;
		Long verifiedMemberId = 1L;

		when(notificationRepository.findById(notificationId)).thenReturn(
			Optional.of(Notification.builder()
				.id(1L)
				.isRead(false)
				.memberId(1L)
				.build()
			)
		);

		when(notificationRepository.save(any())).thenReturn(
			Notification.builder()
				.id(1L)
				.isRead(true)
				.memberId(1L)
				.build()
		);

		//when
		UpdateNotificationStatusResDto response = notificationService.updateNotificationStatus(
			notificationId, verifiedMemberId
		);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getNotificationId()).isEqualTo(notificationId);
		assertThat(response.isRead()).isEqualTo(true);
	}

	@Test
	@DisplayName("알림 상태 수정 - 대상 알림 정보가 존재하지 않는 경우 예외 테스트")
	void updateNotificationTest_Exception1() {
		//given
		Long notificationId = 1L;
		Long verifiedMemberId = 1L;

		when(notificationRepository.findById(notificationId))
			.thenThrow(new NotificationException(ExceptionCode.NOT_FOUND_NOTIFICATION));

		//when & then
		assertThatThrownBy(() ->
			notificationService.updateNotificationStatus(notificationId, verifiedMemberId))
			.isInstanceOf(NotificationException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_NOTIFICATION.getMessage());
	}

	@Test
	@DisplayName("알림 상태 수정 - 수정 권한 예외 테스트")
	void updateNotificationTest_Exception2() {
		//given
		Long notificationId = 1L;
		Long verifiedMemberId = 1L;

		when(notificationRepository.findById(notificationId)).thenReturn(
			Optional.of(Notification.builder()
				.id(1L)
				.isRead(false)
				.memberId(2L)
				.build()
			)
		);

		//when & then
		assertThatThrownBy(() ->
			notificationService.updateNotificationStatus(notificationId, verifiedMemberId))
			.isInstanceOf(NotificationException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_NOTIFICATION.getMessage());
	}

	@Test
	@DisplayName("알림 삭제 - 정상 테스트")
	void deleteNotificationTest_Success() {
		//given
		Long notificationId = 1L;
		Long verifiedNotificationId = 1L;

		Notification notification = Notification.builder()
			.id(1L)
			.isRead(true)
			.memberId(1L)
			.build();
		notificationRepository.save(notification);

		when(notificationRepository.findById(notificationId)).thenReturn(
			Optional.of(notification)
		);

		//when
		notificationService.deleteNotification(notificationId, verifiedNotificationId);

		//then
		assertThat(notificationRepository.count()).isEqualTo(0);
	}

	@Test
	@DisplayName("알림 삭제 - 대상 알림이 존재하지 않는 경우 예외 테스트")
	void deleteNotificationTest_Exception1() {
		//given
		Long notificationId = 1L;
		Long verifiedNotificationId = 1L;

		when(notificationRepository.findById(notificationId))
			.thenThrow(new NotificationException(ExceptionCode.NOT_FOUND_NOTIFICATION));

		//when & then
		assertThatThrownBy(() ->
			notificationService.deleteNotification(notificationId, verifiedNotificationId))
			.isInstanceOf(NotificationException.class)
			.hasMessage(ExceptionCode.NOT_FOUND_NOTIFICATION.getMessage());
	}

	@Test
	@DisplayName("알림 삭제 - 권한 예외 테스트")
	void deleteNotificationTest_Exception2() {
		//given
		Long notificationId = 1L;
		Long verifiedNotificationId = 1L;

		Notification notification = Notification.builder()
			.id(1L)
			.isRead(true)
			.memberId(2L)
			.build();

		when(notificationRepository.findById(notificationId)).thenReturn(
			Optional.of(notification)
		);

		//when & then
		assertThatThrownBy(() ->
			notificationService.deleteNotification(notificationId, verifiedNotificationId))
			.isInstanceOf(NotificationException.class)
			.hasMessage(ExceptionCode.NOT_OWNER_NOTIFICATION.getMessage());
	}

	@Test
	@DisplayName("알림 삭제 - 삭제 가능 상태 예외 테스트")
	void deleteNotificationTest_Exception3() {
		//given
		Long notificationId = 1L;
		Long verifiedNotificationId = 1L;

		Notification notification = Notification.builder()
			.id(1L)
			.isRead(false)
			.memberId(1L)
			.build();

		when(notificationRepository.findById(notificationId)).thenReturn(
			Optional.of(notification)
		);

		//when & then
		assertThatThrownBy(() ->
			notificationService.deleteNotification(notificationId, verifiedNotificationId))
			.isInstanceOf(NotificationException.class)
			.hasMessage(ExceptionCode.NOT_READ_NOTIFICATION.getMessage());
	}
}
