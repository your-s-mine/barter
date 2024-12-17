package com.barter.domain.chat.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageReqDto {

	private String roomId;
	private String message;
	private String time;

	@Builder
	public ChatMessageReqDto(String roomId, String message) {
		this.roomId = roomId;
		this.message = message;
		this.time = LocalDateTime.now().toString();
	}

}
