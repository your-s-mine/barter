package com.barter.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VerifiedMember {
	private final Long id;
	private final String email;

	@Builder
	public VerifiedMember(Long id, String email) {
		this.id = id;
		this.email = email;
	}
}
