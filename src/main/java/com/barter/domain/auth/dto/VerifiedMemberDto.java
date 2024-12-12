package com.barter.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VerifiedMemberDto {
	private Long id;
	private String email;

	@Builder
	public VerifiedMemberDto(Long id, String email) {
		this.id = id;
		this.email = email;
	}
}
