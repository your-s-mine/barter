package com.barter.domain.trade.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum DonationResult {
	SUCCESS("SUCCESS", "나눔 요청에 성공하였습니다."),
	FAIL("FAIL", "나눔 요청에 실패하였습니다.");

	private final String status;
	private final String message;

	DonationResult(String status, String message) {
		this.status = status;
		this.message = message;
	}
}
