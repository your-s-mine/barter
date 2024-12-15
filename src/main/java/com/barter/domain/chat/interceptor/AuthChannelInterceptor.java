package com.barter.domain.chat.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.barter.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT == accessor.getCommand()) {
			// 연결 시 인증
			String authToken = accessor.getFirstNativeHeader("Authorization");
			log.info("accessor Headers :{}", accessor.getMessageHeaders());
			log.info("Auth token: {}", authToken);
			jwtUtil.validateToken(authToken);

			String userId = jwtUtil.getMemberClaims(authToken).getSubject();
			log.info("userId: {}", userId);

		}

		// 추가로 SUBSCRIBE, DISCONNECT 와 같은 로직도 여기서 구현 가능하다.

		return message;
	}
}
