package com.barter.domain.chat.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.chat.dto.request.CreateChatRoomReqDto;
import com.barter.domain.chat.dto.response.ChatMessageResDto;
import com.barter.domain.chat.dto.response.CreateChatRoomResDto;
import com.barter.domain.chat.dto.response.FindChatRoomResDto;
import com.barter.domain.chat.service.ChatLogService;
import com.barter.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final ChatLogService chatLogService;

	@PostMapping
	public ResponseEntity<CreateChatRoomResDto> createChatRoom(VerifiedMember member,
		@RequestBody CreateChatRoomReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createChatRoom(member, reqDto));
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<PagedModel<ChatMessageResDto>> findChatsByRoom(@PathVariable String roomId,
		@PageableDefault Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(chatLogService.findChatsByRoom(roomId, pageable));
	}

	@GetMapping
	public ResponseEntity<PagedModel<FindChatRoomResDto>> findRoomsByMember(VerifiedMember member,
		@PageableDefault Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(chatRoomService.findRoomsByMember(member, pageable));
	}
}
