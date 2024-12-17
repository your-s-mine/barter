package com.barter.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.chat.collections.ChattingContent;
import com.barter.domain.chat.dto.ChatMessageDto;
import com.barter.domain.chat.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final SimpMessageSendingOperations template;
	private final ChattingRepository chattingRepository;

	@MessageMapping("/send-message")
	public void sendMessage(@Payload ChatMessageDto chatMessageDto, StompHeaderAccessor headerAccessor) {

		log.info("headerAccessor : {}", headerAccessor);
		String userId = (String)headerAccessor.getSessionAttributes().get("userId");
		String roomId = (String)headerAccessor.getSessionAttributes().get("roomId");
		log.info("userId : {}", userId);

		ChattingContent chattingContent = ChattingContent.builder()
			.roomId(roomId)
			.message(chatMessageDto.getMessage())
			.userId(Long.valueOf(userId))
			.build();

		chattingRepository.save(chattingContent);

		// 일단 이거는 한명이 나가도 유지되어야 함 (나중에 채팅 로그 저장하면 해당 로그를 보여줄 수 있음)
		log.info("CHAT : {}", chatMessageDto.getMessage());
		template.convertAndSend("/topic/chat/room/" + roomId, chatMessageDto);

	}
}
