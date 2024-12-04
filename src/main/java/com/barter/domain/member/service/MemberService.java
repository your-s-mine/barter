package com.barter.domain.member.service;

import org.springframework.stereotype.Service;

import com.barter.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
}
