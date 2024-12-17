package com.barter.domain.chat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import com.barter.domain.chat.dto.response.ChatMessageResDto;
import com.barter.domain.chat.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatLogService {

	private final ChattingRepository chattingRepository;

	public PagedModel<ChatMessageResDto> findChatsByRoom(String roomId, Pageable pageable) {

		Page<ChatMessageResDto> chatMessageResDtos = chattingRepository.findByRoomIdOrderByChatTimeDesc(roomId,
			pageable).map(ChatMessageResDto::from);
		return new PagedModel<>(chatMessageResDtos);

	}

}
