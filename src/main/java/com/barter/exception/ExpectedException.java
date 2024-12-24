package com.barter.exception;

import org.springframework.http.HttpStatus;

import com.barter.exception.enums.ExceptionCode;

import lombok.Getter;

@Getter
public class ExpectedException extends RuntimeException {

	private final ExceptionCode exceptionCode;

	public ExpectedException(ExceptionCode exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	@Override
	public String getMessage() {
		return exceptionCode.getMessage();
	}

	public HttpStatus getExceptionCode() {
		return exceptionCode.getCode();
	}
}
