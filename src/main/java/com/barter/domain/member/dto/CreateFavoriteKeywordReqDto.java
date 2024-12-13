package com.barter.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateFavoriteKeywordReqDto {
	private String keyword;

	@Builder
	public CreateFavoriteKeywordReqDto(String keyword) {
		this.keyword = keyword;
	}
}
