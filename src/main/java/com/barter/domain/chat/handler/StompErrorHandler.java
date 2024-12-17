package com.barter.domain.chat.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

	@Override
	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
		// 예외에 따라 클라이언트에 보낼 커스텀 메시지 생성
		String errorMessage = "An unexpected error occurred.";
		Throwable rootCause = ex; // 예외의 루트 원인

		if (ex instanceof MessageDeliveryException) {
			rootCause = ex.getCause(); // 실제 원인 예외를 추출
			log.info("Root cause of the error: {}", rootCause.getClass().getName());
		}

		// IllegalArgumentException 예외 추출
		if (rootCause instanceof IllegalArgumentException) {
			log.info("Caught IllegalArgumentException: {}", rootCause.getMessage());
			errorMessage = "Invalid subscription: " + rootCause.getMessage();
		} else if (rootCause instanceof IllegalStateException) {
			log.info("Caught IllegalStateException: {}", rootCause.getMessage());
			errorMessage = "Server state issue: " + rootCause.getMessage();
		}

		// STOMP ERROR 메시지 헤더 구성
		StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
		accessor.setMessage(errorMessage); // 클라이언트가 읽을 기본 메시지
		accessor.addNativeHeader("error-code", "4001"); // 커스텀 에러 코드 추가 가능
		accessor.addNativeHeader("error-message", errorMessage); // 상세 메시지 추가

		// TODO : 추후 응답 메시지 정리 할 필요 있음

		log.info("헤더 정보 : {}", accessor);

		// 클라이언트에 보낼 메시지 생성
		return MessageBuilder.createMessage(
			errorMessage.getBytes(), // 에러 내용을 바이트 배열로 변환
			accessor.getMessageHeaders()
		);
	}
}
