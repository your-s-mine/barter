package com.barter.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.entity.MemberFavoriteKeyword;

public interface MemberFavoriteKeywordRepository extends JpaRepository<MemberFavoriteKeyword, Long> {
	boolean existsByMemberAndFavoriteKeyword(Member member, FavoriteKeyword favoriteKeyword);

	int countByMember(Member member);

	List<MemberFavoriteKeyword> findByMember(Member member);
}
