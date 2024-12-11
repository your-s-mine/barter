package com.barter.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.chat.entity.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
}
