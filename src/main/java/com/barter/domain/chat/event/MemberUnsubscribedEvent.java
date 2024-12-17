package com.barter.domain.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUnsubscribedEvent {
	private final Long memberId;
	private final String roomId;
}
