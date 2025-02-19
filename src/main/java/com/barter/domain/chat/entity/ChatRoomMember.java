package com.barter.domain.chat.entity;

import com.barter.domain.chat.enums.JoinStatus;
import com.barter.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CHAT_ROOM_MEMBER")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	private ChatRoom chatRoom;

	@Enumerated(EnumType.STRING)
	private JoinStatus joinStatus;

	public static ChatRoomMember create(final Member member, final ChatRoom chatRoom) {

		return ChatRoomMember.builder()
			.member(member)
			.chatRoom(chatRoom)
			.joinStatus(JoinStatus.PENDING)
			.build();
	}

	public void changeJoinStatus(JoinStatus joinStatus) {
		this.joinStatus = joinStatus;
	}
}
