package com.barter.domain.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.barter.domain.auth.dto.SignInReqDto;
import com.barter.domain.auth.dto.SignInResDto;
import com.barter.domain.auth.dto.SignUpReqDto;
import com.barter.domain.auth.exception.DuplicateEmailException;
import com.barter.domain.auth.exception.InvalidCredentialsException;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.oauth.dto.LoginOAuthMemberDto;
import com.barter.domain.oauth.dto.LoginOAuthMemberResDto;
import com.barter.domain.oauth.enums.OAuthProvider;
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
		if (memberRepository.existsByEmail(req.getEmail())) {
			throw new DuplicateEmailException("중복된 이메일입니다.");
		}

		String hashedPassword = passwordEncoder.encode(req.getPassword());
		Member signUpMember = Member.createBasicMember(req.getEmail(), hashedPassword, req.getNickname());
		memberRepository.save(signUpMember);
	}

	public SignInResDto signIn(SignInReqDto req) {
		Member member = memberRepository.findByEmail(req.getEmail())
			.orElseThrow(() -> new InvalidCredentialsException("존재하지 않는 사용자입니다."));
		if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
			throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
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
			.password(passwordEncoder.encode(uuid + ""))
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
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
	}

}
