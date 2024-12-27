package com.barter.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.auth.dto.FindAddressResDto;
import com.barter.domain.auth.dto.SignInReqDto;
import com.barter.domain.auth.dto.SignInResDto;
import com.barter.domain.auth.dto.SignUpReqDto;
import com.barter.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpReqDto req) {
		authService.signUp(req);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInResDto> login(@RequestBody @Valid SignInReqDto req) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(authService.signIn(req));
	}

	@GetMapping("/address/{memberId}")
	public FindAddressResDto findAddress(@PathVariable Long memberId) {
		return authService.findAddress(memberId);
	}

	// 회원 정보 조회 (주석 처리 유지)
	// @GetMapping("/member/{memberId}")
	// public ResponseEntity<SignInResDto> getMemberInfo(VerifiedMember verifiedMember) {
	// 	SignInResDto memberInfo = authService.getMemberInfo(verifiedMember);
	// 	return ResponseEntity.ok(memberInfo);
	// }
}
