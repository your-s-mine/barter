package com.barter.domain.oauth.dto;

import com.barter.domain.oauth.enums.OAuthProvider;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginOAuthMemberDto {

	private OAuthProvider provider;
	private String id;
	private String nickname;

	@Builder
	public LoginOAuthMemberDto(OAuthProvider provider, String id, String nickname) {
		this.provider = provider;
		this.id = id;
		this.nickname = nickname;
	}
}
