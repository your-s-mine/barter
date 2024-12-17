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
	private String content;
	private Long userId;

	@Builder
	public ChattingContent(String roomId, String content, Long userId) {
		this.roomId = roomId;
		this.content = content;
		this.userId = userId;
	}
}
