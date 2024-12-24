package com.barter.exception.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
	// 인증, 인가
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
	NO_AUTHORITY(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
	// 나눔 교환
	INVALID_END_DATE(HttpStatus.BAD_REQUEST, "종료일자는 오늘로부터 7일 이내만 가능합니다."),
	DUPLICATE_REQUEST(HttpStatus.CONFLICT, "이미 요청한 유저가 존재합니다."),
	ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "이미 마감된 나눔입니다."),
	NOT_FOUND_DONATION_TRADE(HttpStatus.NOT_FOUND, "존재하지 않는 나눔 교환입니다."),
	// 관심 키워드
	DUPLICATE_FAVORITE_KEYWORD(HttpStatus.CONFLICT, "이미 관심키워드로 등록돼 있습니다."),
	MAX_FAVORITE_KEYWORDS_EXCEEDED(HttpStatus.BAD_REQUEST, "관심 키워드는 3개까지만 등록 가능합니다."),
	NOT_FOUND_FAVORITE_KEYWORD(HttpStatus.NOT_FOUND, "존재하지 않는 관심 키워드 입니다.");

	private final HttpStatus code;
	private final String message;
}
