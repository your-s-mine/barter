package com.barter.domain.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.barter.domain.chat.collections.ChattingContent;

public interface ChattingRepository extends MongoRepository<ChattingContent, String> {
	List<ChattingContent> findByRoomId(String roomId);
}
