package com.barter.domain.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.barter.domain.chat.dto.request.ChatMessageReqDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

	private final KafkaTemplate<String, ChatMessageReqDto> kafkaTemplate;

	@Value("${kafka.topic.chat}")
	private String topic;

	public void sendMessageToKafka(ChatMessageReqDto message) {
		kafkaTemplate.send(topic, message);
	}

}
