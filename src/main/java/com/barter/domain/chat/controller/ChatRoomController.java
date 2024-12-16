package com.barter.domain.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.chat.dto.request.CreateChatRoomReqDto;
import com.barter.domain.chat.dto.response.CreateChatRoomResDto;
import com.barter.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping
	public ResponseEntity<CreateChatRoomResDto> createChatRoom(VerifiedMember member,
		@RequestBody CreateChatRoomReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createChatRoom(member, reqDto));
	}
}
