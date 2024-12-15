package com.barter.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.chat.entity.ChatRoom;
import com.barter.domain.chat.enums.RoomStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomResDto {
	private String roomId;
	private String suggestMemberNickname;
	private String registerMemberNickname;
	private RoomStatus roomStatus;
	private LocalDateTime createdAt;

	public static CreateChatRoomResDto of(ChatRoom chatRoom, String suggestMemberNickname,
		String registerMemberNickname) {

		return CreateChatRoomResDto.builder()
			.roomId(chatRoom.getId())
			.suggestMemberNickname(suggestMemberNickname)
			.registerMemberNickname(registerMemberNickname)
			.createdAt(LocalDateTime.now())
			.roomStatus(chatRoom.getRoomStatus())
			.build();
	}
}
