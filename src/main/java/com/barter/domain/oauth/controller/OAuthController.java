package com.barter.domain.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.oauth.enums.OAuthProvider;
import com.barter.domain.oauth.service.OAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

	private final OAuthService oauthService;

	@GetMapping("/callback/{provider}")
	public void callback(
		@PathVariable OAuthProvider provider,
		@RequestParam("code") String authorizationCode
	) {
	}
}
