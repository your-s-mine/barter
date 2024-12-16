package com.barter.domain.notification;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;

import com.barter.domain.notification.dto.response.FindNotificationResDto;
import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.enums.NotificationType;
import com.barter.domain.notification.respository.NotificationRepository;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.enums.TradeType;

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
}
