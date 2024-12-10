package com.barter.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_FAVORITE_KEYWORDS")
public class MemberFavoriteKeyword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Member member;
	@ManyToOne
	private FavoriteKeyword favoriteKeyword;

	@Builder
	public MemberFavoriteKeyword(Long id, Member member, FavoriteKeyword favoriteKeyword) {
		this.id = id;
		this.member = member;
		this.favoriteKeyword = favoriteKeyword;
	}

	public void validateAuthority(Long memberId) {
		if (!member.isEqualsId(memberId)) {
			throw new IllegalStateException("권한이 없습니다.");
		}
	}
}
