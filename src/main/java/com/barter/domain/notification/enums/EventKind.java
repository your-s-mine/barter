package com.barter.domain.notification.enums;

import lombok.Getter;

@Getter
public enum EventKind {

	DEFAULT("Dummy-Event", "Subscribe Completed!"),

	// ACTIVITY
	IMMEDIATE_TRADE_SUGGEST("즉시 교환, 제안 신청", " 교환에 제안이 들어왔어요!"),
	IMMEDIATE_TRADE_SUGGEST_ACCEPT("즉시 교환, 제안 수락", " 교환에 신청한 제안이 수락되었어요!"),
	IMMEDIATE_TRADE_SUGGEST_CANCEL("즉시 교환, 제안 취소", " 교환의 제안 승낙이 취소되었어요."),
	IMMEDIATE_TRADE_SUGGEST_DENY("즉시 교환, 제안 거절", " 교환에 신청한 제안이 거절되었어요."),
	IMMEDIATE_TRADE_COMPLETE("즉시 교환, 교환 완료", " 교환을 마쳤습니다!"),

	PERIOD_TRADE_SUGGEST("기간 교환, 제안 신청", " 교환에 제안이 들어왔어요!"),
	PERIOD_TRADE_SUGGEST_ACCEPT("기간 교환, 제안 수락", " 교환에 신청한 제안이 수락되었어요!"),
	PERIOD_TRADE_SUGGEST_DENY("기간 교환, 제안 거절", " 교환에 신청한 제안이 거절되었어요."),
	PERIOD_TRADE_CLOSE("기간 교환, 제안 수락", " 교환이 종료되었습니다."),
	PERIOD_TRADE_COMPLETE("기간 교환, 제안 수락", " 교환을 마쳤습니다!"),

	DONATION_TRADE_SUGGEST("나눔 교환, 나눔 신청", " 을(를) 누군가 나눔받았어요!"),

	// KEYWORD
	KEYWORD("키워드 알림", "키워드에 해당하는 교환이 추가되었습니다!"),
	;

	private final String eventName;
	private final String eventMessage;

	EventKind(String eventName, String eventMessage) {
		this.eventName = eventName;
		this.eventMessage = eventMessage;
	}

	public static String completeEventMessage(EventKind eventKind, String targetName) {
		return targetName + eventKind.getEventMessage();
	}
}
