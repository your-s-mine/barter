package com.barter.config;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.barter.domain.chat.dto.request.ChatMessageReqDto;

@EnableKafka
@Configuration
public class KafkaProducerConfiguration {

	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String producer_bootstrapServers;

	// Kafka ProducerFactory 생성 Bean 메서드
	@Bean
	public ProducerFactory<String, ChatMessageReqDto> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigurations());
	}

	// Kafka Producer 구성을 위한 설정값들을 포함한 맵을 반환하는 메서드
	@Bean
	public Map<String, Object> producerConfigurations() {
		Map<String, Object> producerConfigurations;
		producerConfigurations = Map.of(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer_bootstrapServers,
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
		);

		return producerConfigurations;
	}

	// KafkaTemplate 을 생성하는 Bean 메서드

	@Bean
	public KafkaTemplate<String, ChatMessageReqDto> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
