package com.barter.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.chat.dto.ChatMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final SimpMessageSendingOperations template;

	// @MessageMapping("/enter-user")
	// public void enterUser(@Payload ChatMessageDto chatMessageDto, SimpMessageHeaderAccessor headerAccessor) {
	//
	// 	// chatRoom 상태가 변화해야 한다. (OPEN -> IN_PROGRESS) : 두 명 모두 들어온 경우
	//
	// 	String userId = (String)headerAccessor.getSessionAttributes().get("userId");
	//
	// 	chatRoomService.changeRoomStatus(chatMessageDto.getRoomId());
	// 	chatRoomService.updateMemberJoinStatus(chatMessageDto.getRoomId(), Long.valueOf(userId), JoinStatus.IN_ROOM);
	//
	// 	// 해당 방의 유저에게 입장을 알림
	// 	chatMessageDto.setMessage(userId + "님이 입장!");
	// 	template.convertAndSend("/topic/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
	// }

	@MessageMapping("/send-message")
	public void sendMessage(@Payload ChatMessageDto chatMessageDto, MessageHeaderAccessor headerAccessor) {

		// 일단 이거는 한명이 나가도 유지되어야 함 (나중에 채팅 로그 저장하면 해당 로그를 보여줄 수 있음)
		log.info("CHAT : {}", chatMessageDto.getMessage());
		template.convertAndSend("/topic/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);

	}
}
