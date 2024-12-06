package com.barter.domain.auth.service;

import com.barter.domain.auth.dto.LoginRequestDto;
import com.barter.domain.auth.dto.LoginResponseDto;
import com.barter.domain.auth.dto.SignUpRequestDto;
import com.barter.domain.auth.exception.DuplicateEmailException;
import com.barter.domain.auth.exception.InvalidCredentialsException;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void register(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new DuplicateEmailException("중복된 이메일입니다.");
        }

        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        Member member = Member.builder()
                .email(signUpRequestDto.getEmail())
                .password(hashedPassword)
                .nickname(signUpRequestDto.getNickname()) // 변경된 부분
                .build();

        memberRepository.save(member);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 이메일로 사용자 조회
        Optional<Member> optionalMember = memberRepository.findByEmail(loginRequestDto.getEmail());
        if (optionalMember.isEmpty()) {
            throw new InvalidCredentialsException("존재하지 않는 사용자입니다.");
        }

        Member member = optionalMember.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공
        return new LoginResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                "로그인 성공"
        );
    }
}
