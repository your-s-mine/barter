package com.barter.config;

import com.barter.domain.auth.service.AddressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${kakao.api-key}")
    private String apiKey;

    @Bean
    public AddressService addressService(WebClient.Builder builder) {
        return new AddressService(builder, apiKey);
    }
}
