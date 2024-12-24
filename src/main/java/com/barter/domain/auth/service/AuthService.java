package com.barter.domain.auth.service;

import static com.barter.exception.enums.ExceptionCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.barter.domain.auth.dto.SignInReqDto;
import com.barter.domain.auth.dto.SignInResDto;
import com.barter.domain.auth.dto.SignUpReqDto;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.dto.LoginOAuthMemberResDto;
import com.barter.domain.oauth.enums.OAuthProvider;
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.customexceptions.MemberException;
import com.barter.security.JwtUtil;
import com.barter.security.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public void signUp(SignUpReqDto req) {
		// 이메일 중복 검증
		if (memberRepository.existsByEmail(req.getEmail())) {
			throw new AuthException(DUPLICATE_EMAIL);
		}

		// 비밀번호 암호화 후 저장
		String hashedPassword = passwordEncoder.encode(req.getPassword());
		Member signUpMember = Member.createBasicMember(req.getEmail(), hashedPassword, req.getNickname());
		memberRepository.save(signUpMember);
	}

	public SignInResDto signIn(SignInReqDto req) {
		Member member = memberRepository.findByEmail(req.getEmail())
			.orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
		if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
			throw new AuthException(INVALID_PASSWORD);
		}
		String token = jwtUtil.createToken(member.getId(), member.getEmail());
		return SignInResDto.builder()
			.accessToken(token)
			.build();
	}

	public void signupWithOAuth(OAuthProvider provider, LoginOAuthMemberDto memberInfo) {
		UUID uuid = UUID.randomUUID();
		Member socialMember = Member.builder()
			.provider(provider)
			.providerId(memberInfo.getId())
			.email(memberInfo.getEmail())
			.password(passwordEncoder.encode(uuid.toString()))
			.nickname(memberInfo.getNickname())
			.build();
		memberRepository.save(socialMember);
	}

	public LoginOAuthMemberResDto signinWithOAuth(OAuthProvider provider, LoginOAuthMemberDto memberInfo) {
		return memberRepository.findByProviderAndProviderId(provider, memberInfo.getId())
			.map(member -> {
				String accessToken = jwtUtil.createToken(member.getId(), member.getEmail());
				return LoginOAuthMemberResDto.builder()
					.accessToken(accessToken)
					.build();
			})
			.orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
	}
}
