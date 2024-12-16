package com.barter.domain.oauth.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.oauth.dto.LoginOAuthMemberResDto;
import com.barter.domain.oauth.enums.OAuthProvider;
import com.barter.domain.oauth.service.OAuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

	private final OAuthService oauthService;

	@GetMapping("/login/{provider}")
	public void redirectLoginPage(
		@PathVariable OAuthProvider provider,
		HttpServletResponse response
	) throws IOException {
		String loginPageUrl = oauthService.generateLoginPageUrl(provider);
		response.sendRedirect(loginPageUrl);
	}

	@GetMapping("/callback/{provider}")
	public ResponseEntity<LoginOAuthMemberResDto> callback(
		@PathVariable OAuthProvider provider,
		@RequestParam("code") String authorizationCode
	) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(oauthService.signupOrSignin(provider, authorizationCode));
	}
}
