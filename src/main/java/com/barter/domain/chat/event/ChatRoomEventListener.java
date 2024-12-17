package com.barter.domain.chat.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.barter.domain.chat.dto.response.ChatMessageResDto;
import com.barter.domain.chat.enums.JoinStatus;
import com.barter.domain.chat.service.ChatLogService;
import com.barter.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomEventListener {

	private final ChatRoomService chatRoomService;
	private final ChatLogService chatLogService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@EventListener
	public void handleUserSubscribedEvent(MemberSubscribedEvent event) {
		log.info("멤버 채팅방 구독 >> userId : {}, roomId : {}", event.getMemberId(), event.getRoomId());

		try {
			chatRoomService.changeRoomStatus(event.getRoomId());
			chatRoomService.updateMemberJoinStatus(event.getRoomId(), event.getMemberId(), JoinStatus.IN_ROOM);

			List<ChatMessageResDto> chatLogs = chatLogService.findChatLogs(event.getRoomId());
			log.info("chatLogs: {}", chatLogs);
			simpMessagingTemplate.convertAndSendToUser(String.valueOf(event.getMemberId()),
				"/topic/chat/room/" + event.getRoomId(),
				chatLogs);
			// TODO :  해당 유저가 반복적으로 방을 나갔다 들어오는 경우에 대한 성능 개선 필요 (ex : cache)

			log.info("멤버 채팅방 구독 성공 : {} 유저가 {} 방에 구독 중", event.getMemberId(), event.getRoomId());

		} catch (Exception e) {
			log.error("구독 에러 이벤트 발생 : {}", e.getMessage());
			throw e;
		}
	}

	@EventListener
	public void handleUserUnsubscribedEvent(MemberUnsubscribedEvent event) {
		log.info("멤버 채팅방 구독 취소 >> userId : {}, roomId : {}", event.getMemberId(), event.getRoomId());

		try {
			chatRoomService.updateMemberJoinStatus(event.getRoomId(), event.getMemberId(), JoinStatus.LEAVE);

			log.info("멤버 채팅방 구독 취소 : {} 유저가 {} 방 구독 취소", event.getMemberId(), event.getRoomId());

		} catch (Exception e) {
			log.error("구독 취소 에러 이벤트 발생 : {}", e.getMessage());
			throw e;
		}

	}
}
