package com.barter.domain.member.controller;

import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
}
