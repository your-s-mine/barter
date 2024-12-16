package com.barter.domain.notification;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
public class SseEmittersTest {

	@InjectMocks
	private SseEmitters sseEmitters;

	@Test
	@DisplayName("SseEmitter 저장 - 성공 테스트")
	void saveEmitterTest_Success() {
		//given
		Long verifiedMemberId = 1L;

		//when
		SseEmitter savedEmitter = sseEmitters.saveEmitter(verifiedMemberId);

		//then
		assertThat(savedEmitter).isNotNull();
	}
}
