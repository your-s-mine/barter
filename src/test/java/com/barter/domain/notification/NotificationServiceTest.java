package com.barter.domain.notification;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.notification.dto.response.UpdateNotificationStatusResDto;
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
}
