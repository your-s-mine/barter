package com.barter.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.common.KeywordHelper;
import com.barter.domain.member.dto.CreateFavoriteKeywordReqDto;
import com.barter.domain.member.dto.FindFavoriteKeywordResDto;
import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.entity.MemberFavoriteKeyword;
import com.barter.domain.member.repository.FavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberFavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteKeywordService {

	private static final int MAX_KEYWORD_COUNT = 3;
	private final MemberRepository memberRepository;
	private final FavoriteKeywordRepository favoriteKeywordRepository;
	private final MemberFavoriteKeywordRepository memberFavoriteKeywordRepository;

	@Transactional
	public void createFavoriteKeyword(Long memberId, CreateFavoriteKeywordReqDto req) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
		String keyword = KeywordHelper.removeSpace(req.getKeyword());
		FavoriteKeyword favoriteKeyword = favoriteKeywordRepository.findByKeyword(keyword)
			.orElseGet(() -> favoriteKeywordRepository.save(
				FavoriteKeyword.builder()
					.keyword(keyword)
					.build()));
		if (memberFavoriteKeywordRepository.existsByMemberAndFavoriteKeyword(member, favoriteKeyword)) {
			throw new IllegalStateException("이미 관심키워드로 등록돼 있습니다.");
		}
		if (memberFavoriteKeywordRepository.countByMember(member) >= MAX_KEYWORD_COUNT) {
			throw new IllegalStateException("관심 키워드는 3개까지만 등록 가능합니다.");
		}
		MemberFavoriteKeyword memberFavoriteKeyword = MemberFavoriteKeyword.builder()
			.member(member)
			.favoriteKeyword(favoriteKeyword)
			.build();
		memberFavoriteKeywordRepository.save(memberFavoriteKeyword);
	}

	public List<FindFavoriteKeywordResDto> findFavoriteKeywords(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
		return memberFavoriteKeywordRepository.findByMember(member).stream()
			.map(FindFavoriteKeywordResDto::from)
			.toList();
	}

	public void deleteFavoriteKeyword(Long memberId, Long memberFavoriteKeywordId) {
		MemberFavoriteKeyword memberFavoriteKeyword = memberFavoriteKeywordRepository.findById(memberFavoriteKeywordId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관심 키워드 입니다."));
		memberFavoriteKeyword.validateAuthority(memberId);
		memberFavoriteKeywordRepository.delete(memberFavoriteKeyword);
	}
}
