package com.barter.domain.chat.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageReqDto {

	private String roomId;
	private String message;

	// for 응답 속도 테스트 (나중에 하나만 남길 예정)
	private String sentTime;
	private String receivedTime;

	@Builder
	public ChatMessageReqDto(String roomId, String message) {
		this.roomId = roomId;
		this.message = message;
		this.sentTime = LocalDateTime.now().toString();
	}

	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}

	public void setReceivedTime(String receivedTimetime) {
		this.receivedTime = receivedTimetime;
	}

}
