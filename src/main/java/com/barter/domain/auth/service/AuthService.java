package com.barter.domain.auth.service;

import com.barter.domain.auth.dto.*;
import org.springframework.stereotype.Service;

import com.barter.domain.auth.exception.DuplicateEmailException;
import com.barter.domain.auth.exception.InvalidCredentialsException;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
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

}
