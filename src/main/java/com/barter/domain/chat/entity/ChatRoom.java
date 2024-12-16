package com.barter.domain.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.barter.domain.chat.enums.RoomStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CHAT_ROOM")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

	@Id
	private String id;

	private LocalDateTime createdAt;

	private Long personCount; // 추후 확장성 고려

	@Enumerated(EnumType.STRING)
	private RoomStatus roomStatus;

	public static ChatRoom create() {
		return ChatRoom.builder()
			.id(UUID.randomUUID().toString())
			.createdAt(LocalDateTime.now())
			.personCount(2L)
			.roomStatus(RoomStatus.OPEN)
			.build();
	}

}
