package com.barter.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResDto {

	private final String accessToken;

	@Builder
	public SignInResDto(String accessToken) {
		this.accessToken = accessToken;
	}
}
