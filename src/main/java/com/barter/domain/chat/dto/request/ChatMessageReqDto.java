package com.barter.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageReqDto {

	private String roomId;
	private String message;

	@Builder
	public ChatMessageReqDto(String roomId, String message) {
		this.roomId = roomId;
		this.message = message;
	}

}
