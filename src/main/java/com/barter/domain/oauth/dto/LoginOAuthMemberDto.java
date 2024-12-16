package com.barter.domain.oauth.dto;

import com.barter.domain.oauth.enums.OAuthProvider;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginOAuthMemberDto {

	private OAuthProvider provider;
	private String id;
	private String email;
	private String nickname;

	@Builder
	public LoginOAuthMemberDto(OAuthProvider provider, String id, String email, String nickname) {
		this.provider = provider;
		this.id = id;
		this.email = email;
		this.nickname = nickname;
	}
}
