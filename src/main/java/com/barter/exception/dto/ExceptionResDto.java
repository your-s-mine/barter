package com.barter.exception.dto;

import org.springframework.http.HttpStatus;

import com.barter.exception.ExpectedException;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExceptionResDto {

	private final String message;
	private final HttpStatus code;

	@Builder
	public ExceptionResDto(String message, HttpStatus code) {
		this.message = message;
		this.code = code;
	}

	public static ExceptionResDto of(ExpectedException exception) {
		return ExceptionResDto.builder()
			.message(exception.getMessage())
			.code(exception.getExceptionCode())
			.build();
	}
}
