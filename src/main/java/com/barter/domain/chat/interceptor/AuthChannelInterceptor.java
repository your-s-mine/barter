package com.barter.domain.chat.interceptor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.barter.domain.chat.event.MemberSubscribedEvent;
import com.barter.domain.chat.event.MemberUnsubscribedEvent;
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
	private final ApplicationEventPublisher eventPublisher;
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
			log.info("accessor:{}", accessor);
			String userId = (String)accessor.getSessionAttributes().get("userId");

			log.info("User {} is subscribing to {}", userId, destination);

			if (destination != null && destination.startsWith("/topic/chat/room")) {
				String roomId = destination.split("/topic/chat/room/")[1];
				log.info("subscribed roomId: {}", roomId);

				accessor.getSessionAttributes().put("roomId", roomId);

				eventPublisher.publishEvent(new MemberSubscribedEvent(Long.valueOf(userId), roomId));

			}
		}

		if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {
			String subscriptionId = accessor.getSubscriptionId();
			log.info("accessor:{}", subscriptionId);
			String userId = (String)accessor.getSessionAttributes().get("userId");

			log.info("User {} is unsubscribing from {}", userId, subscriptionId);
			String roomId = (String)accessor.getSessionAttributes().get("roomId");
			log.info("roomId: {}", roomId);

			if (subscriptionId != null && subscriptionId.startsWith("sub-")) {
				log.info("unsubscribed roomId: {}", roomId);
				eventPublisher.publishEvent(new MemberUnsubscribedEvent(Long.valueOf(userId), roomId));

			}

		}

		log.info("message: {}", message);
		return message;
	}
}
