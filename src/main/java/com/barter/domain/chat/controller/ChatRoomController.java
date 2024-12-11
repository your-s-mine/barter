package com.barter.domain.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.chat.dto.request.CreateChatRoomReqDto;
import com.barter.domain.chat.dto.response.CreateChatRoomResDto;
import com.barter.domain.chat.service.ChatRoomService;
import com.barter.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping
	// 일단 member 를 가져왔습니다. (현재 인증된 멤버에 대한 정보 : id, email, nickname)
	public ResponseEntity<CreateChatRoomResDto> createChatRoom(Member member,
		@RequestBody CreateChatRoomReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createChatRoom(member, reqDto));
	}
}
