package com.barter.domain.chat.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

	private String roomId;
	private String message;
	private String time;

	@Builder
	public ChatMessageDto(String roomId, String nickname,
		String message) {
		this.roomId = roomId;
		this.message = message;
		this.time = LocalDateTime.now().toString();
	}
}
