package com.barter.domain.notification.respository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepositoryImpl implements SseEmitterRepository {

	private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

	@Override
	public SseEmitter saveEmitter(Long emitterKey, SseEmitter emitter) {
		emitterMap.put(emitterKey, emitter);
		return emitter;
	}

	@Override
	public SseEmitter findByKey(Long emitterKey) {
		return emitterMap.get(emitterKey);
	}

	@Override
	public void deleteEmitter(Long emitterKey) {
		emitterMap.remove(emitterKey);
	}
}
