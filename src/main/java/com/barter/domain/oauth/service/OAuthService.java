package com.barter.domain.oauth.service;

import org.springframework.stereotype.Service;

import com.barter.domain.auth.service.AuthService;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.dto.LoginOAuthMemberResDto;
import com.barter.domain.oauth.enums.OAuthProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final OAuthClientService oAuthClientService;
	private final MemberRepository memberRepository;
	private final AuthService authService;

	public LoginOAuthMemberResDto signupOrSignin(OAuthProvider provider, String authorizationCode) {
		LoginOAuthMemberDto memberInfo = oAuthClientService.signin(provider, authorizationCode);
		if (!memberRepository.existsByProviderId(memberInfo.getId())) {
			authService.signupWithOAuth(provider, memberInfo);
		}
		return authService.signinWithOAuth(provider, memberInfo);
	}

	public String generateLoginPageUrl(OAuthProvider provider) {
		return oAuthClientService.generateLoginPageUrl(provider);
	}
}
