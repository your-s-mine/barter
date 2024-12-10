package com.barter.domain.member.dto;

import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.MemberFavoriteKeyword;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindFavoriteKeywordResDto {
	private Long id;
	private String keyword;

	@Builder
	public FindFavoriteKeywordResDto(Long id, String keyword) {
		this.id = id;
		this.keyword = keyword;
	}

	public static FindFavoriteKeywordResDto from(MemberFavoriteKeyword memberFavoriteKeyword) {
		FavoriteKeyword favoriteKeyword = memberFavoriteKeyword.getFavoriteKeyword();
		return FindFavoriteKeywordResDto.builder()
			.id(favoriteKeyword.getId())
			.keyword(favoriteKeyword.getKeyword())
			.build();
	}
}
