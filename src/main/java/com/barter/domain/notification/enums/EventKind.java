package com.barter.domain.notification.enums;

import lombok.Getter;

@Getter
public enum EventKind {

	DEFAULT("Dummy-Event", "Subscribe Completed!"),

	// ACTIVITY
	IMMEDIATE_TRADE_SUGGEST("즉시 교환, 제안 신청", " 교환에 제안이 들어왔어요!"),
	IMMEDIATE_TRADE_SUGGEST_ACCEPT("즉시 교환, 제안 수락", " 교환에 신청한 제안이 수락되었어요!"),
	IMMEDIATE_TRADE_SUGGEST_DENY("즉시 교환, 제안 거절", " 교환에 신청한 제안이 거절되었어요."),
	IMMEDIATE_TRADE_COMPLETE("즉시 교환, 교환 완료", " 교환을 마쳤습니다!"),
	;

	private final String eventName;
	private final String eventMessage;

	EventKind(String eventName, String eventMessage) {
		this.eventName = eventName;
		this.eventMessage = eventMessage;
	}

	public static String completeEventMessage(EventKind eventKind, String tradeTitle) {
		return tradeTitle + eventKind.getEventMessage();
	}
}
