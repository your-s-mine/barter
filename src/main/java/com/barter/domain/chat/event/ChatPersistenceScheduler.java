package com.barter.domain.chat.event;

import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.barter.domain.chat.collections.ChattingContent;
import com.barter.domain.chat.repository.ChattingRepository;
import com.barter.domain.chat.service.ChatCachingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatPersistenceScheduler {
	private final ChatCachingService chatCachingService;
	private final ChattingRepository chattingRepository;

	@Scheduled(fixedRate = 60000)
	public void persistChatMessages() {
		Set<String> keys = chatCachingService.getAllKeys();

		for (String key : keys) {
			String roomId = key.replace("chat_room:", "");

			// 캐시된 메시지 가져오기
			List<ChattingContent> messages = chatCachingService.getCachedMessages(roomId);
			if (messages != null && !messages.isEmpty()) {
				chattingRepository.saveAll(messages);

				chatCachingService.deleteCachedMessages(roomId);
			}
		}
	}
}
