package com.barter.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.chat.entity.ChatRoomMember;
import com.barter.domain.chat.enums.JoinStatus;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
	long countByChatRoomIdAndJoinStatus(String roomId, JoinStatus joinStatus);

	Optional<ChatRoomMember> findByChatRoomIdAndMemberId(String roomId, Long memberId);

	Page<ChatRoomMember> findAllByMemberId(Long id, Pageable pageable);
}
