package com.barter.domain.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
		// 입력값 검증
		validateSignUpInput(req);

		// 이메일 중복 검증
		if (memberRepository.existsByEmail(req.getEmail())) {
			throw new DuplicateEmailException("중복된 이메일입니다.");
		}

		// 비밀번호 암호화 후 저장
		String hashedPassword = passwordEncoder.encode(req.getPassword());
		Member signUpMember = Member.createBasicMember(req.getEmail(), hashedPassword, req.getNickname());
		memberRepository.save(signUpMember);
	}

	private void validateSignUpInput(SignUpReqDto req) {
		// 이메일 검증
		if (!StringUtils.hasText(req.getEmail()) || !req.getEmail().matches("^.+@.+\\..+$")) {
			throw new IllegalArgumentException("유효한 이메일 형식이어야 합니다.");
		}

		// 비밀번호 길이 및 조건 검증
		if (!StringUtils.hasText(req.getPassword()) || req.getPassword().length() < 8) {
			throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
		}
		if (!req.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
			throw new IllegalArgumentException("비밀번호는 숫자와 문자를 포함해야 합니다.");
		}

		// 닉네임 검증
		if (!StringUtils.hasText(req.getNickname())) {
			throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
		}
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
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
	}
}
