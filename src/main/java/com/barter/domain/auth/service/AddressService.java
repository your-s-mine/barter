package com.barter.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.barter.domain.member.entity.Address;

@Service
public class AddressService {

	private final WebClient webClient;
	private final String apiKey;

	// 사용자 정의 생성자
	public AddressService(WebClient.Builder builder, @Value("${kakao.api-key}") String apiKey) {
		this.webClient = builder.baseUrl("https://dapi.kakao.com").build();
		this.apiKey = apiKey;
	}

	public String getFormattedAddress(String query) {
		String url = UriComponentsBuilder.fromUriString("/v2/local/search/address.json")
			.queryParam("query", query)
			.build()
			.toUriString();

		try {
			return webClient.get()
				.uri(url)
				.header("Authorization", "KakaoAK " + apiKey)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		} catch (WebClientResponseException e) {
			System.err.println("HTTP Status Code: " + e.getStatusCode());
			System.err.println("Response Body: " + e.getResponseBodyAsString());
			throw e;
		}
	}

	public Address createAddressEntity(String postNum, String address1, String address2) {
		return Address.builder()
			.postNum(postNum)
			.address1(address1)
			.address2(address2)
			.build();
	}
}
