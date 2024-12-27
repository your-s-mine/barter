package com.barter.domain.chat.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.chat.collections.ChattingContent;
import com.barter.domain.chat.dto.request.ChatMessageReqDto;
import com.barter.domain.chat.service.ChatCachingService;
import com.barter.domain.chat.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final KafkaProducerService kafkaProducerService;
	private final ChatCachingService chatCachingService;

	@MessageMapping("/send-message")
	public void sendMessage(@Payload ChatMessageReqDto chatMessageReqDto, StompHeaderAccessor headerAccessor) {

		log.info("headerAccessor : {}", headerAccessor);
		String userId = (String)headerAccessor.getSessionAttributes().get("userId");
		String roomId = (String)headerAccessor.getSessionAttributes().get("roomId");
		log.info("userId : {}", userId);

		ChattingContent chattingContent = ChattingContent.builder()
			.roomId(roomId)
			.message(chatMessageReqDto.getMessage())
			.userId(Long.valueOf(userId))
			.chatTime(LocalDateTime.now())
			.build();

		chatCachingService.cacheMessage(roomId, chattingContent);
		kafkaProducerService.sendMessageToKafka(chatMessageReqDto);

		log.info("CHAT : {}", chatMessageReqDto.getMessage());
	}
}
