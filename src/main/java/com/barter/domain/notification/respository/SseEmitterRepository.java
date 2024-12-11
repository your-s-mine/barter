package com.barter.domain.notification.respository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {

	SseEmitter saveEmitter(Long emitterKey, SseEmitter sseEmitter);

	SseEmitter findByKey(Long emitterKey);

	void deleteEmitter(Long emitterKey);
}
