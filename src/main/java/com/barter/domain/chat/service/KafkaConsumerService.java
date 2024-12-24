package com.barter.domain.chat.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.barter.domain.chat.dto.request.ChatMessageReqDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

	private final SimpMessageSendingOperations messagingTemplate;

	@KafkaListener(topics = "${kafka.topic.chat}", groupId = "chatGroup")
	public void listenChatMessage(ChatMessageReqDto chatMessageReqDto) {
		// kafka 에서 메시지를 받으면 해당 메시지를 STOMP 로 전달

		try {
			String roomId = chatMessageReqDto.getRoomId();
			messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, chatMessageReqDto);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
