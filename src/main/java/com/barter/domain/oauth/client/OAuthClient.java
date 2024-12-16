package com.barter.domain.oauth.client;

import org.springframework.stereotype.Component;

import com.barter.domain.oauth.dto.OAuthLoginMemberInfo;
import com.barter.domain.oauth.enums.OAuthProvider;

@Component
public interface OAuthClient {

	String getAccessToken(String authorizationCode);

	OAuthLoginMemberInfo getMemberInfo(String accessToken);

	boolean supports(OAuthProvider provider);

}
