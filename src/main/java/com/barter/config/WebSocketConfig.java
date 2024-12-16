package com.barter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.barter.domain.chat.handler.StompErrorHandler;
import com.barter.domain.chat.interceptor.AuthChannelInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final AuthChannelInterceptor authChannelInterceptor;
	private final StompErrorHandler stompErrorHandler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		// 메시지 구독 url (topic 구독)
		registry.enableSimpleBroker("/topic");

		// 메시지 발행 url, @MessageMapping 어노테이션과 연결됨
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		// 웹소켓 (STOMP)  접속 주소 설정

		registry.addEndpoint("/ws")
			.setAllowedOrigins("*");

		registry.setErrorHandler(stompErrorHandler);

	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(authChannelInterceptor);
	}
}
