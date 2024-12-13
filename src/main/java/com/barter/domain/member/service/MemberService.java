package com.barter.domain.member.service;

import com.barter.domain.auth.dto.MemberInfoResDto;
import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberInfoResDto findMemberInfo(VerifiedMember verifiedMember) {
		Member member = memberRepository.findById(verifiedMember.getId())
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		return MemberInfoResDto.builder()
				.id(member.getId())
				.email(member.getEmail())
				.nickname(member.getNickname())
				.build();
	}
}
