package com.barter.domain.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSubscribedEvent {
	private final Long memberId;
	private final String roomId;
}
