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

	// 물품
	NOT_FOUND_REGISTERED_PRODUCT(HttpStatus.NOT_FOUND, "존재하지 않는 등록물품입니다."),
	NOT_FOUND_SUGGESTED_PRODUCT(HttpStatus.NOT_FOUND, "존재하지 않는 제안물품입니다."),
	NOT_OWNER_REGISTERED_PRODUCT(HttpStatus.FORBIDDEN, "등록물품을 생성한 회원이 아닙니다."),
	NOT_OWNER_SUGGESTED_PRODUCT(HttpStatus.FORBIDDEN, "제안물품을 생성한 회원이 아닙니다."),
	REGISTERED_PRODUCT_INFO_UPDATE_IMPOSSIBLE(HttpStatus.BAD_REQUEST, "PENDING 상태일 때, 등록물품 정보를 수정할 수 있습니다."),
	SUGGESTED_PRODUCT_INFO_UPDATE_IMPOSSIBLE(HttpStatus.BAD_REQUEST, "PENDING 상태일 때, 제안물품 정보를 수정할 수 있습니다."),
	NOT_VALID_STATUS_REGISTERED_PRODUCT_DELETE(
		HttpStatus.BAD_REQUEST, "PENDING 또는 COMPLETED 상태인 경우에만 등록 물품을 삭제할 수 있습니다."
	),
	NOT_SUPPORT_REGISTERED_STATUS(HttpStatus.BAD_REQUEST, "지원하지 않는 등록물품 상태입니다."),
	NOT_VALID_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "1~3개 사이의 이미지를 가질 수 있습니다.");

	private final HttpStatus code;
	private final String message;
}
