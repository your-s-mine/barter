package com.barter.domain.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginOAuthMemberResDto {

	private String accessToken;

	@Builder
	public LoginOAuthMemberResDto(String accessToken) {
		this.accessToken = accessToken;
	}
}
