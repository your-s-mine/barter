package com.barter.domain.notification;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
	@DisplayName("알림 구독 - 성공 테스트")
	void subscribeTest_Success() {
		//given
		Long verifiedMemberId = 1L;

		SseEmitter newEmitter = new SseEmitter();
		Mockito.when(sseEmitters.saveEmitter(verifiedMemberId))
			.thenReturn(newEmitter);

		//when
		SseEmitter savedEmitter = notificationService.subscribe(verifiedMemberId);

		//then
		assertThat(savedEmitter).isNotNull();
		assertThat(savedEmitter).isEqualTo(newEmitter);
	}
}
