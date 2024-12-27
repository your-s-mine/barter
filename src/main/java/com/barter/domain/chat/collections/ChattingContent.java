package com.barter.domain.chat.collections;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime chatTime;

	@Builder
	public ChattingContent(String roomId, String message, Long userId, LocalDateTime chatTime) {
		this.roomId = roomId;
		this.message = message;
		this.userId = userId;
		this.chatTime = chatTime;
	}
}
