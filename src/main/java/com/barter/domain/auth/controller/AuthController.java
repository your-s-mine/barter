package com.barter.domain.auth.controller;

import com.barter.domain.auth.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}


