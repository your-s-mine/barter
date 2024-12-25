package com.barter.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.barter.domain.chat.dto.request.ChatMessageReqDto;

@EnableKafka
@Configuration
public class KafkaListenerConfiguration {

	@Value("${spring.kafka.consumer.bootstrap-servers}")
	private String listener_bootstrapServers;

	// KafkaListener 컨테이너 팩토리를 생성하는 Bean 메서드
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, ChatMessageReqDto> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, ChatMessageReqDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	// Kafka ConsumerFactory 를 생성하는 Bean 메서드
	@Bean
	public ConsumerFactory<String, ChatMessageReqDto> consumerFactory() {
		JsonDeserializer<ChatMessageReqDto> deserializer = new JsonDeserializer<>();

		deserializer.addTrustedPackages("*"); // 모든 패키지 신뢰

		// Kafka Consumer 구성을 위한 설정 값들을 설정
		Map<String, Object> consumerConfigurations;

		consumerConfigurations = Map.of(
			ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, listener_bootstrapServers,
			ConsumerConfig.GROUP_ID_CONFIG, "adopt",
			ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer,
			ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);

	}
}
