package com.barter.domain.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.chat.dto.request.CreateChatRoomReqDto;
import com.barter.domain.chat.dto.response.CreateChatRoomResDto;
import com.barter.domain.chat.entity.ChatRoom;
import com.barter.domain.chat.entity.ChatRoomMember;
import com.barter.domain.chat.repository.ChatRoomMemberRepository;
import com.barter.domain.chat.repository.ChatRoomRepository;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomMemberRepository chatRoomMemberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;

	// member 는 임시입니다.
	// 현재는 방 생성과 동시에 1명의 유저가 초대 되는 느낌이어서 아래와 같이 표현 하였습니다.
	// 만약 방을 생성하고 유저를 초대하는 방식이라면 api 가 분리될 필요가 있다고 봅니다.
	@Transactional
	public CreateChatRoomResDto createChatRoom(VerifiedMember member, CreateChatRoomReqDto reqDto) {

		Member registerMember = memberRepository.findById(reqDto.getRegisterMemberId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

		Member suggestMember = memberRepository.findById(member.getId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

		if (registerMember.equals(suggestMember)) {
			throw new IllegalArgumentException("자기 자신을 초대할 수는 없습니다.");
		}

		// 채팅방 생성
		ChatRoom chatRoom = ChatRoom.create();
		chatRoomRepository.save(chatRoom);

		// suggestMember 저장
		ChatRoomMember memberChatRoomMember = ChatRoomMember.create(suggestMember, chatRoom);

		chatRoomMemberRepository.save(memberChatRoomMember);

		// registerMember 저장
		ChatRoomMember sellerRoomMember = ChatRoomMember.create(registerMember, chatRoom);

		chatRoomMemberRepository.save(sellerRoomMember);

		return CreateChatRoomResDto.of(chatRoom, suggestMember.getNickname(), registerMember.getNickname());

	}
}
