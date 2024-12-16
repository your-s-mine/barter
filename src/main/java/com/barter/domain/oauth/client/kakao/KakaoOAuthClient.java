package com.barter.domain.oauth.client.kakao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import com.barter.domain.oauth.client.OAuthClient;
import com.barter.domain.oauth.client.kakao.dto.KakaoLoginMemberInfoResDto;
import com.barter.domain.oauth.client.kakao.dto.KakaoTokenResDto;
import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.enums.OAuthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient implements OAuthClient {

	private final static String AUTH_SERVER_BASE_URL = "https://kauth.kakao.com";
	private final static String RESOURCE_SERVER_BASE_URL = "https://kapi.kakao.com";

	@Value("${oauth2.kakao.client_id}")
	private String clientId;
	@Value("${oauth2.kakao.redirect_url}")
	private String redirectUrl;

	private final RestClient restClient;

	@Override
	public String generateLoginPageUrl() {
		return AUTH_SERVER_BASE_URL
			+ "/oauth/authorize"
			+ "?client_id=" + clientId
			+ "&redirect_uri=" + redirectUrl
			+ "&response_type=" + "code";
	}

	@Override
	public String getAccessToken(String authorizationCode) {
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", clientId);
		body.add("code", authorizationCode);

		return Optional.of(
				restClient.post()
					.uri(AUTH_SERVER_BASE_URL + "/oauth/token")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(body)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, resp) -> {
						throw new RuntimeException("카카오 AccessToken 조회 실패");
					})
					.body(KakaoTokenResDto.class)
			)
			.map(KakaoTokenResDto::getAccessToken)
			.orElseThrow(() -> new RuntimeException("카카오 AccessToken 조회 실패"));
	}

	@Override
	public LoginOAuthMemberDto getMemberInfo(String accessToken) {
		return Optional.of(
				restClient.get()
					.uri(RESOURCE_SERVER_BASE_URL + "/v2/user/me")
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, resp) -> {
						throw new RuntimeException("카카오 UserInfo 조회 실패");
					})
					.body(KakaoLoginMemberInfoResDto.class))
			.map(response -> LoginOAuthMemberDto.builder()
				.id(response.getId() + "")
				.provider(OAuthProvider.KAKAO)
				.email(response.getKakaoAccount().getEmail())
				.nickname(response.getProperties().getNickname())
				.build()
			)
			.orElseThrow(() -> new RuntimeException("카카오 UserInfo 조회 실패"));
	}

	@Override
	public boolean supports(OAuthProvider provider) {
		return provider == OAuthProvider.KAKAO;
	}
}