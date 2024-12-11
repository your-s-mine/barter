package com.barter.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
