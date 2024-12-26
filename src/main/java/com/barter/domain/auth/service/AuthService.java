package com.barter.domain.auth.service;

import static com.barter.exception.enums.ExceptionCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.barter.domain.auth.dto.SignInReqDto;
import com.barter.domain.auth.dto.SignInResDto;
import com.barter.domain.auth.dto.SignUpReqDto;
import com.barter.domain.member.entity.Address;
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

		// 비밀번호 암호화
		String hashedPassword = passwordEncoder.encode(req.getPassword());

		// Address 객체 생성 (주소 정보 포함)
		Address address = Address.builder()
				.postNum(req.getPostNum())
				.address1(req.getAddress1())
				.address2(req.getAddress2())
				.build();

		// Member 객체 생성 (주소 포함)
		Member member = Member.builder()
				.email(req.getEmail())
				.password(hashedPassword)
				.nickname(req.getNickname())
				.address(address) // 주소 추가
				.build();

		// 저장
		memberRepository.save(member);
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
