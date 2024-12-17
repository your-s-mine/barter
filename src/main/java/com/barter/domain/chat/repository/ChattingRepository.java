package com.barter.domain.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.barter.domain.chat.collections.ChattingContent;

public interface ChattingRepository extends MongoRepository<ChattingContent, String> {
	Page<ChattingContent> findByRoomIdOrderByChatTimeDesc(String roomId, Pageable pageable);
}
