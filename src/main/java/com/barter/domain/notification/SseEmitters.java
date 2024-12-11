package com.barter.domain.notification.respository;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitters {

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

	private void sendEvent(Long memberId, String eventName, Object data) {
		SseEmitter emitter = sseEmitterRepository.findByKey(memberId);
		if (emitter == null) {
			// 전달 대상 사용자의 SseEmitter 가 없다면 접속하지 않은 사용자이므로 이벤트 전달 중지
			log.info("전달 대상 사용자 접속하지 않음");
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.id(String.valueOf(memberId))
				.name(eventName)
				.data(data)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
