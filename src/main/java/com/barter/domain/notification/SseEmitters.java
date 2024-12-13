package com.barter.domain.notification;

import static com.barter.domain.notification.enums.EventKind.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SseEmitters {

	private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
	private static final long TIMEOUT = 60 * 60 * 1000;

	public SseEmitter saveEmitter(Long memberId) {
		SseEmitter emitter = new SseEmitter(TIMEOUT);
		emitterMap.put(memberId, emitter);

		setCallbacks(memberId, emitter);

		sendEvent(memberId, DEFAULT.getName(), DEFAULT.getMessage());

		return emitter;
	}

	public void sendEvent(Long memberId, String eventName, Object data) {
		SseEmitter emitter = emitterMap.get(memberId);
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

	private void setCallbacks(Long memberId, SseEmitter emitter) {
		emitter.onTimeout(() -> {
			emitter.complete();
			log.info("타임아웃으로 SseEmitter 연결해제, emitterKey={}", memberId);
		});

		emitter.onError(error -> {
			emitter.complete();
			log.info("클라이언트 연결해제로 인해 SseEmitter 연결해제, emitterKey={}", memberId);
		});

		emitter.onCompletion(() -> {
			emitterMap.remove(memberId);
			log.info("연결해제된 SseEmitter 를 Local-Memory 에서 삭제, emitterKey={}", memberId);
		});
	}
}
