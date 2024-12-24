package com.barter.config;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
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
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
			// 파티셔닝 전략 추가 (주석을 해야 일정한 파티션 분배 가능)
			//ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class
		);

		return producerConfigurations;
	}

	// KafkaTemplate 을 생성하는 Bean 메서드

	@Bean
	public KafkaTemplate<String, ChatMessageReqDto> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
