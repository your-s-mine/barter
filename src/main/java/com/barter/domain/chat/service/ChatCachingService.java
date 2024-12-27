package com.barter.domain.chat.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.barter.domain.chat.collections.ChattingContent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatCachingService {

	@Qualifier("customRedisTemplate")
	private final RedisTemplate<String, ChattingContent> redisTemplate;

	private static final String CHAT_CACHE_KEY = "chat_room:";

	public ChatCachingService(RedisTemplate<String, ChattingContent> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void cacheMessage(String roomId, ChattingContent message) {
		String key = CHAT_CACHE_KEY + roomId;
		redisTemplate.opsForList().rightPush(key, message);
		List<ChattingContent> cachedMessages = redisTemplate.opsForList().range(key, 0, -1);
		cachedMessages.forEach(c -> log.info("Cached Message: {}", c));
		redisTemplate.expire(key, Duration.ofMinutes(3)); // 3분동안 캐시에 유지
	}

	public List<ChattingContent> getCachedMessages(String roomId) {
		String key = CHAT_CACHE_KEY + roomId;
		return redisTemplate.opsForList().range(key, 0, -1);
	}

	public void deleteCachedMessages(String roomId) {
		String key = CHAT_CACHE_KEY + roomId;
		redisTemplate.delete(key);
	}

	public Set<String> getAllKeys() {
		return redisTemplate.keys(CHAT_CACHE_KEY + "*");
	}
}
