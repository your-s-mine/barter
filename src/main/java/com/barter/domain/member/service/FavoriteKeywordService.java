package com.barter.domain.member.service;

import static com.barter.exception.enums.ExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.common.KeywordHelper;
import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.dto.CreateFavoriteKeywordReqDto;
import com.barter.domain.member.dto.FindFavoriteKeywordResDto;
import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.entity.MemberFavoriteKeyword;
import com.barter.domain.member.repository.FavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberFavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.customexceptions.FavoriteKeywordException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteKeywordService {

	private static final int MAX_KEYWORD_COUNT = 3;
	private final MemberRepository memberRepository;
	private final FavoriteKeywordRepository favoriteKeywordRepository;
	private final MemberFavoriteKeywordRepository memberFavoriteKeywordRepository;

	@Transactional
	public void createFavoriteKeyword(VerifiedMember verifiedMember, CreateFavoriteKeywordReqDto req) {
		Member member = memberRepository.findById(verifiedMember.getId())
			.orElseThrow(() -> new AuthException(NOT_FOUND_MEMBER));
		String keyword = KeywordHelper.removeSpace(req.getKeyword());
		FavoriteKeyword favoriteKeyword = favoriteKeywordRepository.findByKeyword(keyword)
			.orElseGet(() -> favoriteKeywordRepository.save(FavoriteKeyword.builder()
				.keyword(keyword)
				.build()));
		if (memberFavoriteKeywordRepository.existsByMemberAndFavoriteKeyword(member, favoriteKeyword)) {
			throw new FavoriteKeywordException(DUPLICATE_FAVORITE_KEYWORD);
		}
		if (memberFavoriteKeywordRepository.countByMember(member) >= MAX_KEYWORD_COUNT) {
			throw new FavoriteKeywordException(MAX_FAVORITE_KEYWORDS_EXCEEDED);
		}
		MemberFavoriteKeyword memberFavoriteKeyword = MemberFavoriteKeyword.builder()
			.member(member)
			.favoriteKeyword(favoriteKeyword)
			.build();
		memberFavoriteKeywordRepository.save(memberFavoriteKeyword);
	}

	public List<FindFavoriteKeywordResDto> findFavoriteKeywords(VerifiedMember verifiedMember) {
		Member member = memberRepository.findById(verifiedMember.getId())
			.orElseThrow(() -> new AuthException(NOT_FOUND_MEMBER));
		return memberFavoriteKeywordRepository.findByMember(member).stream()
			.map(FindFavoriteKeywordResDto::from)
			.toList();
	}

	public void deleteFavoriteKeyword(VerifiedMember verifiedMember, Long memberFavoriteKeywordId) {
		MemberFavoriteKeyword memberFavoriteKeyword = memberFavoriteKeywordRepository.findById(memberFavoriteKeywordId)
			.orElseThrow(() -> new FavoriteKeywordException(NOT_FOUND_FAVORITE_KEYWORD));
		memberFavoriteKeyword.validateAuthority(verifiedMember.getId());
		memberFavoriteKeywordRepository.delete(memberFavoriteKeyword);
	}
}
