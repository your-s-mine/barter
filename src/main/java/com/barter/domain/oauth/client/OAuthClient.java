package com.barter.domain.oauth.client;

import org.springframework.stereotype.Component;

import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.enums.OAuthProvider;

@Component
public interface OAuthClient {

	String generateLoginPageUrl();

	String getAccessToken(String authorizationCode);

	LoginOAuthMemberDto getMemberInfo(String accessToken);

	boolean supports(OAuthProvider provider);

}
