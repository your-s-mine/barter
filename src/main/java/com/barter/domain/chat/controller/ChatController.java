package com.barter.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final SimpMessageSendingOperations template;

	@MessageMapping("/enter-user")
	public void enterUser(SimpMessageHeaderAccessor headerAccessor) {

		System.out.println("userId = " + headerAccessor.getHeader("simpSessionAttributes"));

		template.convertAndSend("/topic/chat/room", "hello everyOne");
	}
}
