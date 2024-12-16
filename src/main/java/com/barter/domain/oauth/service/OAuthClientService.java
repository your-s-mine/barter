package com.barter.domain.oauth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barter.domain.oauth.client.OAuthClient;
import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.enums.OAuthProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthClientService {

	private final List<OAuthClient> clients;

	public LoginOAuthMemberDto signin(OAuthProvider provider, String authorizationCode) {
		OAuthClient oAuth2Client = this.selectClient(provider);
		String accessToken = oAuth2Client.getAccessToken(authorizationCode);
		return oAuth2Client.getMemberInfo(accessToken);
	}

	private OAuthClient selectClient(OAuthProvider provider) {
		return clients.stream()
			.filter(clients -> clients.supports(provider))
			.findFirst()
			.orElseThrow();
	}
}
