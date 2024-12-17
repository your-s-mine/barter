package com.barter.domain.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.chat.entity.ChatRoomMember;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindChatRoomResDto {
	private String roomId;
	private LocalDateTime createdAt;

	@Builder
	public FindChatRoomResDto(String roomId, LocalDateTime createdAt) {
		this.roomId = roomId;
		this.createdAt = createdAt;
	}

	public static List<FindChatRoomResDto> from(List<ChatRoomMember> chatRoomMembers) {

		return chatRoomMembers.stream()
			.map(chatRoomMember ->
				FindChatRoomResDto.builder()
					.roomId(chatRoomMember.getChatRoom().getId())
					.createdAt(chatRoomMember.getChatRoom().getCreatedAt())
					.build()
			).toList();

	}

}
