package com.barter.domain.chat.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
	public enum MessageType {
		TALK, LEAVE
	}

	private MessageType messageType;
	private String roomId;
	private String nickname;
	private String message;
	private String time;

	@Builder
	public ChatMessageDto(MessageType messageType, String roomId, String nickname,
		String message) {
		this.messageType = messageType;
		this.roomId = roomId;
		this.nickname = nickname;
		this.message = message;
		this.time = LocalDateTime.now().toString();
	}
}
