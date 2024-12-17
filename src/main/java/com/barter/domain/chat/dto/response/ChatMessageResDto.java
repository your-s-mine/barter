package com.barter.domain.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.chat.collections.ChattingContent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageResDto {

	private String roomId;
	private String message;
	private LocalDateTime chatTime;

	@Builder
	public ChatMessageResDto(String roomId, String message, LocalDateTime time) {
		this.roomId = roomId;
		this.message = message;
		this.chatTime = time;
	}

	@Override
	public String toString() {
		return "ChatMessageDto [roomId=" + roomId + ", message=" + message + ", chatTime=" + chatTime + "]";
	}

	public static List<ChatMessageResDto> from(List<ChattingContent> chats) {

		return chats.stream()
			.map(chat -> ChatMessageResDto.builder()
				.roomId(chat.getRoomId())
				.message(chat.getMessage())
				.time(chat.getChatTime())
				.build())
			.toList();
	}
}
