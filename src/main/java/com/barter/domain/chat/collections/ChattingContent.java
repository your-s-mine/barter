package com.barter.domain.chat.collections;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "barter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChattingContent {
	@Id
	private String id;
	private String roomId;
	private String message;
	private Long userId;
	private String chatTime;

	@Builder
	public ChattingContent(String roomId, String message, Long userId, String chatTime) {
		this.roomId = roomId;
		this.message = message;
		this.userId = userId;
		this.chatTime = chatTime;
	}
}
