package com.barter.domain.notification.enums;

import lombok.Getter;

@Getter
public enum EventKind {

	DEFAULT("Dummy-Event", "Subscribe Completed!"),
	NON_CHECKED("Non-Checked-Event", "확인하지 않은 알림이 존재합니다."),
	;

	private final String name;
	private final String message;

	EventKind(String name, String message) {
		this.name = name;
		this.message = message;
	}
}
