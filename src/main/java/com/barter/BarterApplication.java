package com.barter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableCaching
// 해당 경로의 repository 만 MongoDB 리포지토리로 인식 합니다. (안쓰면 INFO 로그에 엄청난 줄이 생깁니다.)
@EnableMongoRepositories(basePackages = "com.barter.domain.chat.repository")
public class BarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarterApplication.class, args);
	}

}
