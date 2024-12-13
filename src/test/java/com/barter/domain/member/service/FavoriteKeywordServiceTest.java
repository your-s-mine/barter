package com.barter.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.dto.CreateFavoriteKeywordReqDto;
import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.entity.MemberFavoriteKeyword;
import com.barter.domain.member.repository.FavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberFavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteKeywordServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FavoriteKeywordRepository favoriteKeywordRepository;

	@Mock
	private MemberFavoriteKeywordRepository memberFavoriteKeywordRepository;

	@InjectMocks
	private FavoriteKeywordService favoriteKeywordService;

	@Test
	@DisplayName("정상적으로 신규 관심 키워드를 등록하는 경우")
	void createFavoriteKeyword_shouldCreateSuccessfully() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateFavoriteKeywordReqDto req = CreateFavoriteKeywordReqDto.builder()
			.keyword("키워드")
			.build();

		Member member = Member.builder()
			.id(1L)
			.build();
		FavoriteKeyword newKeywordEntity = FavoriteKeyword.builder()
			.keyword("키워드")
			.build();

		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(favoriteKeywordRepository.findByKeyword("키워드"))
			.thenReturn(Optional.empty());
		when(favoriteKeywordRepository.save(any(FavoriteKeyword.class)))
			.thenReturn(newKeywordEntity);
		when(memberFavoriteKeywordRepository.existsByMemberAndFavoriteKeyword(member, newKeywordEntity))
			.thenReturn(false);
		when(memberFavoriteKeywordRepository.countByMember(member)).thenReturn(2);

		//when
		favoriteKeywordService.createFavoriteKeyword(verifiedMember, req);

		// then
		verify(memberRepository, times(1)).findById(1L);
		verify(favoriteKeywordRepository, times(1)).findByKeyword("키워드");
		verify(favoriteKeywordRepository, times(1)).save(any(FavoriteKeyword.class));
		verify(memberFavoriteKeywordRepository, times(1)).existsByMemberAndFavoriteKeyword(member, newKeywordEntity);
		verify(memberFavoriteKeywordRepository, times(1)).countByMember(member);
		verify(memberFavoriteKeywordRepository, times(1)).save(any(MemberFavoriteKeyword.class));
	}

	@Test
	@DisplayName("존재하지 않는 멤버에 대해 관심 키워드 등록 시 예외 발생")
	void 존재하지_않는_멤버에_대해_관심_키워드_등록_시_예외_발생() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(999L)
			.build();
		CreateFavoriteKeywordReqDto req = new CreateFavoriteKeywordReqDto("키워드");

		when(memberRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> favoriteKeywordService.createFavoriteKeyword(verifiedMember, req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("존재하지 않는");
	}

	@Test
	@DisplayName("이미 등록된 관심 키워드를 다시 등록하려고 할 경우 예외 발생")
	void 이미_등록된_관심_키워드를_다시_등록하려고_할_경우_예외_발생() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateFavoriteKeywordReqDto req = new CreateFavoriteKeywordReqDto("중복키워드");

		Member member = Member.builder()
			.id(1L)
			.build();
		FavoriteKeyword existingKeyword = FavoriteKeyword.builder()
			.keyword("중복키워드")
			.build();

		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(favoriteKeywordRepository.findByKeyword("중복키워드")).thenReturn(Optional.of(existingKeyword));
		when(memberFavoriteKeywordRepository.existsByMemberAndFavoriteKeyword(member, existingKeyword)).thenReturn(
			true);

		// when & then
		assertThatThrownBy(() -> favoriteKeywordService.createFavoriteKeyword(verifiedMember, req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("이미 관심키워드로");
	}

	@Test
	@DisplayName("관심키워드가 이미 3개 등록된 경우 예외 발생")
	void 관심키워드가_이미_3개_등록된_경우_예외_발생() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		CreateFavoriteKeywordReqDto req = new CreateFavoriteKeywordReqDto("추가키워드");

		Member member = Member.builder()
			.id(1L)
			.build();
		FavoriteKeyword keyword = FavoriteKeyword.builder().keyword("추가키워드").build();

		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(favoriteKeywordRepository.findByKeyword("추가키워드")).thenReturn(Optional.of(keyword));
		when(memberFavoriteKeywordRepository.existsByMemberAndFavoriteKeyword(member, keyword)).thenReturn(false);
		when(memberFavoriteKeywordRepository.countByMember(member)).thenReturn(3);

		// when & then
		assertThatThrownBy(() -> favoriteKeywordService.createFavoriteKeyword(verifiedMember, req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("관심 키워드는 3개까지만");
	}

	@Test
	@DisplayName("정상적으로 관심 키워드를 삭제한다")
	void 정상적으로_관심키워드를_삭제한다() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		Long memberFavoriteKeywordId = 10L;
		Member member = Member.builder()
			.id(1L)
			.build();
		FavoriteKeyword keyword = FavoriteKeyword.builder()
			.id(100L)
			.keyword("키워드")
			.build();
		MemberFavoriteKeyword memberFavoriteKeyword = MemberFavoriteKeyword.builder()
			.id(memberFavoriteKeywordId)
			.member(member)
			.favoriteKeyword(keyword)
			.build();

		when(memberFavoriteKeywordRepository.findById(memberFavoriteKeywordId))
			.thenReturn(Optional.of(memberFavoriteKeyword));

		// when
		favoriteKeywordService.deleteFavoriteKeyword(verifiedMember, memberFavoriteKeywordId);

		// then
		verify(memberFavoriteKeywordRepository, times(1)).findById(memberFavoriteKeywordId);
		verify(memberFavoriteKeywordRepository, times(1)).delete(memberFavoriteKeyword);
	}

	@Test
	@DisplayName("존재하지 않는 관심 키워드를 삭제하려는 경우 예외 발생")
	void 존재하지_않는_관심키워드를_삭제하려는_경우_예외_발생() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(1L)
			.build();
		Long memberFavoriteKeywordId = 999L;

		when(memberFavoriteKeywordRepository.findById(memberFavoriteKeywordId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> favoriteKeywordService.deleteFavoriteKeyword(verifiedMember, memberFavoriteKeywordId))
			.isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	@DisplayName("권한이 없는 사용자가 관심 키워드를 삭제하려는 경우 예외 발생")
	void 권한이_없는_사용자가_관심키워드를_삭제하려는_경우_예외_발생() {
		// given
		VerifiedMember verifiedMember = VerifiedMember.builder()
			.id(2L)
			.build();
		Long memberFavoriteKeywordId = 10L;
		MemberFavoriteKeyword memberFavoriteKeyword = mock(MemberFavoriteKeyword.class);

		when(memberFavoriteKeywordRepository.findById(memberFavoriteKeywordId))
			.thenReturn(Optional.of(memberFavoriteKeyword));
		doThrow(new IllegalArgumentException("권한이 없습니다."))
			.when(memberFavoriteKeyword).validateAuthority(verifiedMember.getId());

		// when & then
		assertThatThrownBy(() -> favoriteKeywordService.deleteFavoriteKeyword(verifiedMember, memberFavoriteKeywordId))
			.isInstanceOf(IllegalArgumentException.class);

		verify(memberFavoriteKeywordRepository, never()).delete(any());
	}
}