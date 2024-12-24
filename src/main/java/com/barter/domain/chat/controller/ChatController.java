package com.barter.domain.chat.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.chat.collections.ChattingContent;
import com.barter.domain.chat.dto.request.ChatMessageReqDto;
import com.barter.domain.chat.repository.ChattingRepository;
import com.barter.domain.chat.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final KafkaProducerService kafkaProducerService;
	private final ChattingRepository chattingRepository;

	@MessageMapping("/send-message")
	public void sendMessage(@Payload ChatMessageReqDto chatMessageReqDto, StompHeaderAccessor headerAccessor) {

		log.info("headerAccessor : {}", headerAccessor);
		String userId = (String)headerAccessor.getSessionAttributes().get("userId");
		String roomId = (String)headerAccessor.getSessionAttributes().get("roomId");
		log.info("userId : {}", userId);

		// Kafka 로 메시지 전송

		// db 저장 시간 측정
		// TODO : 캐싱을 이용해 한번 채팅 시 마다 저장하는 것이 아닌 모아놓았다가 저장하는 형식으로 변경할 예정
		chatMessageReqDto.setSentTime(LocalDateTime.now().toString());

		ChattingContent chattingContent = ChattingContent.builder()
			.roomId(roomId)
			.message(chatMessageReqDto.getMessage())
			.userId(Long.valueOf(userId))
			.build();

		chattingRepository.save(chattingContent);

		// 일단 이거는 한명이 나가도 유지되어야 함 (나중에 채팅 로그 저장하면 해당 로그를 보여줄 수 있음)
		chatMessageReqDto.setReceivedTime(LocalDateTime.now().toString());
		kafkaProducerService.sendMessageToKafka(chatMessageReqDto);

		log.info("CHAT : {}", chatMessageReqDto.getMessage());
	}
}
