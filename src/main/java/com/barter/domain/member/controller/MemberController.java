package com.barter.domain.member.controller;

import com.barter.domain.auth.dto.MemberInfoResDto;
import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/me")
	public ResponseEntity<MemberInfoResDto> findMemberInfo(VerifiedMember verifiedMember) {
		MemberInfoResDto memberInfo = memberService.findMemberInfo(verifiedMember);
		return ResponseEntity.ok(memberInfo);
	}
}
