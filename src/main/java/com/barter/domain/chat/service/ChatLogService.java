package com.barter.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barter.domain.chat.dto.response.ChatMessageResDto;
import com.barter.domain.chat.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatLogService {

	private final ChattingRepository chattingRepository;

	public List<ChatMessageResDto> findChatsByRoom(String roomId) {

		return ChatMessageResDto.from(chattingRepository.findByRoomIdOrderByChatTimeDesc(roomId));

	}

}
