package com.barter.domain.auth.service;

import com.barter.domain.auth.dto.SignUpRequestDto;
import com.barter.domain.auth.exception.DuplicateEmailException;
import com.barter.domain.auth.model.Member;
import com.barter.domain.auth.repository.MemberRepository;
import com.barter.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new DuplicateEmailException("중복된 이메일입니다.");
        }

        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        Member member = new Member(
                null,
                signUpRequestDto.getEmail(),
                hashedPassword,
                signUpRequestDto.getUsername()
        );

        memberRepository.save(member);
    }
}
