package com.barter.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.chat.collections.ChattingContent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageResDto {

	private String roomId;
	private String message;
	private LocalDateTime chatTime;

	@Builder
	public ChatMessageResDto(String roomId, String message, LocalDateTime chatTime) {
		this.roomId = roomId;
		this.message = message;
		this.chatTime = chatTime;
	}

	@Override
	public String toString() {
		return "ChatMessageDto [roomId=" + roomId + ", message=" + message + ", chatTime=" + chatTime + "]";
	}

	public static ChatMessageResDto from(ChattingContent chattingContent) {

		return ChatMessageResDto.builder()
			.roomId(chattingContent.getRoomId())
			.message(chattingContent.getMessage())
			.build();
	}
}
