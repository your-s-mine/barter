package com.barter.domain.chat.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.barter.domain.chat.enums.JoinStatus;
import com.barter.domain.chat.service.ChatRoomService;
import com.barter.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	private final ChatRoomService chatRoomService;
	//private final SimpMessagingTemplate template;

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

			accessor.getSessionAttributes().put("userId", userId);

		}

		// SUBSCRIBE 처리
		if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
			String destination = accessor.getDestination();
			String userId = (String)accessor.getSessionAttributes().get("userId");

			log.info("User {} is subscribing to {}", userId, destination);

			if (destination != null && destination.startsWith("/topic/chat/room")) {
				String roomId = destination.split("/topic/chat/room/")[1];
				log.info("roomId: {}", roomId);

				try {
					chatRoomService.changeRoomStatus(roomId);
					chatRoomService.updateMemberJoinStatus(roomId, Long.valueOf(userId), JoinStatus.IN_ROOM);

				} catch (IllegalArgumentException e) {
					// log.error("Error occurred while subscribing to room: {}", e.getMessage(), e);
					// StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
					// errorAccessor.setMessage("Error: " + e.getMessage());
					// errorAccessor.addNativeHeader("error", e.getMessage());
					// log.info("errorAccessor: {}", errorAccessor);
					// Message<?> errorMessage = MessageBuilder.createMessage("Error: " + e.getMessage(),
					// 	errorAccessor.getMessageHeaders());
					// log.info("errorMessage: {}", errorMessage);
					// 삽 푼 코드
					throw e;
				}

			}
		}

		log.info("message: {}", message);
		return message;
	}
}
