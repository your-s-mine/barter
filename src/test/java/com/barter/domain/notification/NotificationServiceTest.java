package com.barter.domain.notification;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.notification.entity.Notification;
import com.barter.domain.notification.respository.NotificationRepository;
import com.barter.domain.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	@Mock
	private NotificationRepository notificationRepository;
	@Mock
	private SseEmitters sseEmitters;

	@InjectMocks
	private NotificationService notificationService;

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
		Assertions.assertThat(notificationRepository.count()).isEqualTo(0);
	}
}
