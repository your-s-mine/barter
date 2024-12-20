package com.barter.domain.chat.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageReqDto {

	private String roomId;
	private String message;
	private String sentTime;
	private String receivedTime;

	@Builder
	public ChatMessageReqDto(String roomId, String message) {
		this.roomId = roomId;
		this.message = message;
		this.sentTime = LocalDateTime.now().toString();
	}

	public void setReceivedTime(String receivedTimetime) {
		this.receivedTime = receivedTimetime;
	}

}
