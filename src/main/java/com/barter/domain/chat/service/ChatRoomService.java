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
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.repository.TradeProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomMemberRepository chatRoomMemberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final TradeProductRepository tradeProductRepository;

	@Transactional
	public CreateChatRoomResDto createChatRoom(VerifiedMember member, CreateChatRoomReqDto reqDto) {

		Member registerMember = memberRepository.findById(reqDto.getRegisterMemberId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

		Member suggestMember = memberRepository.findById(member.getId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

		if (registerMember.equals(suggestMember)) {
			throw new IllegalArgumentException("자기 자신을 초대할 수는 없습니다.");
		}

		TradeProduct tradeProduct = tradeProductRepository.findById(reqDto.getTradeProductId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 교환 제품 정보가 없습니다."));

		// 채팅방 생성
		ChatRoom chatRoom = ChatRoom.create(tradeProduct);
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
