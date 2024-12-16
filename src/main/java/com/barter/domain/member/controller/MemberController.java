package com.barter.domain.member.controller;

import com.barter.domain.member.dto.FindMemberResDto;
import com.barter.domain.member.dto.UpdateMemberReqDto;
import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	// 회원정보 조회
	@GetMapping("/me")
	public ResponseEntity<FindMemberResDto> findMemberInfo(VerifiedMember verifiedMember) {
		FindMemberResDto memberInfo = memberService.findMemberInfo(verifiedMember);
		return ResponseEntity.ok(memberInfo);
	}

	// 회원정보 수정
	@PutMapping("/me")
	public ResponseEntity<FindMemberResDto> updateMemberInfo(
			VerifiedMember verifiedMember,
			@RequestBody @Valid UpdateMemberReqDto updateRequestDto) {
		FindMemberResDto updatedMember = memberService.updateMemberInfo(verifiedMember, updateRequestDto);
		return ResponseEntity.ok(updatedMember);
	}
}
