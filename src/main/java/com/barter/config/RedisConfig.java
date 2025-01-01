package com.barter.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.barter.domain.chat.collections.ChattingContent;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;

@Configuration
@EnableRedisRepositories(basePackages = "com.barter.domain.chat.repository")
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "customRedisTemplate")
	public RedisTemplate<String, ChattingContent> customRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, ChattingContent> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean(name = "periodTradeRedisTemplate")
	public RedisTemplate<String, List<FindPeriodTradeResDto>> periodTradeRedisTemplate(
		RedisConnectionFactory redisConnectionFactory
	) {
		RedisTemplate<String, List<FindPeriodTradeResDto>> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		template.setKeySerializer(new StringRedisSerializer());

		// List<FindPeriodTradeResDto>에 맞는 직렬화 방식 설정
		Jackson2JsonRedisSerializer<List> serializer =
			new Jackson2JsonRedisSerializer<>(List.class);

		template.setValueSerializer(serializer);

		return template;
	}

	@Bean(name = "periodTradeCountRedisTemplate")
	public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
		return redisTemplate;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter listenerAdapter
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("activity"));
		container.addMessageListener(listenerAdapter, new PatternTopic("keyword"));
		return container;
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter(NotificationService notificationService) {
		return new MessageListenerAdapter(notificationService);
	}
}
