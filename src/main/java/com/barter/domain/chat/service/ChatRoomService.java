package com.barter.domain.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.chat.dto.request.CreateChatRoomReqDto;
import com.barter.domain.chat.dto.response.CreateChatRoomResDto;
import com.barter.domain.chat.entity.ChatRoom;
import com.barter.domain.chat.entity.ChatRoomMember;
import com.barter.domain.chat.enums.JoinStatus;
import com.barter.domain.chat.enums.RoomStatus;
import com.barter.domain.chat.repository.ChatRoomMemberRepository;
import com.barter.domain.chat.repository.ChatRoomRepository;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomMemberRepository chatRoomMemberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final TransactionTemplate transactionTemplate;

	@Transactional
	public CreateChatRoomResDto createChatRoom(VerifiedMember member, CreateChatRoomReqDto reqDto) {

		Member suggestMember = memberRepository.findById(member.getId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

		RegisteredProduct registeredProduct = registeredProductRepository.findById(reqDto.getRegisterProductId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 등록된 물품이 없습니다."));

		if (registeredProduct.getMember().equals(suggestMember)) {
			throw new IllegalArgumentException("자신이 등록한 물품에 대한 채팅은 불가능 합니다.");
		}

		// 채팅방 생성
		ChatRoom chatRoom = ChatRoom.create(reqDto.getRegisterProductId(), registeredProduct.getMember().getId());
		chatRoomRepository.save(chatRoom);

		// suggestMember 저장
		ChatRoomMember memberChatRoomMember = ChatRoomMember.create(suggestMember, chatRoom);

		chatRoomMemberRepository.save(memberChatRoomMember);

		// registerMember 저장
		ChatRoomMember sellerRoomMember = ChatRoomMember.create(registeredProduct.getMember(), chatRoom);

		chatRoomMemberRepository.save(sellerRoomMember);

		return CreateChatRoomResDto.of(chatRoom, suggestMember.getNickname(),
			registeredProduct.getMember().getNickname());

	}

	@Transactional
	public void changeRoomStatus(String roomId) {

		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 입니다."));

		long userCount = chatRoomMemberRepository.countByChatRoomIdAndJoinStatus(roomId, JoinStatus.IN_ROOM);

		if (userCount == 2) {
			chatRoom.updateStatus(RoomStatus.IN_PROGRESS);
		}

	}

	@Transactional(noRollbackFor = {IllegalStateException.class})
	public void updateMemberJoinStatus(String roomId, Long memberId, JoinStatus joinStatus) {
		ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomIdAndMemberId(roomId, memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방 멤버가 없습니다."));

		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 없습니다."));

		if (chatRoom.getRoomStatus() == RoomStatus.CLOSED) {
			chatRoomMember.changeJoinStatus(JoinStatus.LEAVE);
			chatRoomMemberRepository.save(chatRoomMember); // 변경 사항 저장

			// custom exception 으로 수정 예정
			// 닫힌 채팅방에 IN_ROOM 상태의 멤버가 들어오려고 하면 멤버의 상태 LEAVE 상태로 변경
			throw new IllegalStateException("채팅방이 이미 CLOSED 상태입니다.");
		}

		if (chatRoomMember.getJoinStatus() == JoinStatus.PENDING) {
			chatRoom.addMember();
		}

		if (chatRoomMember.getJoinStatus() == JoinStatus.IN_ROOM && joinStatus == JoinStatus.LEAVE) {
			chatRoom.updateStatus(RoomStatus.CLOSED); // 한 명이라도 나가면 CLOSED
		}
		chatRoomMember.changeJoinStatus(joinStatus);
	}
}
