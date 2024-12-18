package com.barter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongodbConfig {

	// 해당 부분은 mongoDB 저장 시 _class 필드가 같이 저장되지 않도록 설정한 코드입니다.
	@Bean
	public MappingMongoConverter mappingMongoConverter(
		MongoDatabaseFactory mongoDatabaseFactory,
		MongoMappingContext mongoMappingContext
	) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return converter;
	}
}
