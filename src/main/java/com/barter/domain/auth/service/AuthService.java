package com.barter.domain.auth.service;

import com.barter.domain.auth.dto.SignUpRequestDto;
import com.barter.domain.auth.exception.DuplicateEmailException;
import com.barter.domain.auth.model.Member;
import com.barter.domain.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthService {

    private final MemberRepository memberRepository;

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void register(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new DuplicateEmailException("중복된 이메일입니다.");
        }

        // 비밀번호 해싱
        String hashedPassword = hashPassword(signUpRequestDto.getPassword());

        // Member 객체 생성
        Member member = new Member(
                signUpRequestDto.getEmail(),
                hashedPassword,
                signUpRequestDto.getUsername()
        );

        memberRepository.save(member);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해싱에 실패했습니다.");
        }
    }
}
