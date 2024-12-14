package com.barter.domain.member.service;

import com.barter.domain.auth.dto.MemberInfoResDto;
import com.barter.domain.auth.dto.MemberUpdateReqDto;
import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	// 회원정보 조회
	public MemberInfoResDto findMemberInfo(VerifiedMember verifiedMember) {
		Member member = memberRepository.findById(verifiedMember.getId())
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		return MemberInfoResDto.builder()
				.id(member.getId())
				.email(member.getEmail())
				.nickname(member.getNickname())
				.profileImageUrl(member.getProfileImageUrl())
				.address(member.getAddress())
				.build();
	}

	// 회원정보 수정
	public MemberInfoResDto updateMemberInfo(VerifiedMember verifiedMember, MemberUpdateReqDto updateRequestDto) {
		Member member = memberRepository.findById(verifiedMember.getId())
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		member.updateInfo(
				updateRequestDto.getNickname(),
				updateRequestDto.getProfileImageUrl(),
				updateRequestDto.getAddress(),
				updateRequestDto.getPassword()
		);

		memberRepository.save(member);

		return MemberInfoResDto.builder()
				.id(member.getId())
				.email(member.getEmail())
				.nickname(member.getNickname())
				.profileImageUrl(member.getProfileImageUrl())
				.address(member.getAddress())
				.build();
	}
}
