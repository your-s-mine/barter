package com.barter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.barter.exception.dto.ExceptionResDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ExpectedException.class)
	public ResponseEntity<ExceptionResDto> handleExpectedException(ExpectedException exception) {
		ExceptionResDto response = ExceptionResDto.of(exception);
		return ResponseEntity
			.status(response.getCode())
			.body(response);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ExceptionResDto> handleUnExpectedException(RuntimeException exception) {
		ExceptionResDto response = ExceptionResDto.builder()
			.code(HttpStatus.INTERNAL_SERVER_ERROR)
			.message(exception.getMessage())
			.build();
		return ResponseEntity
			.status(response.getCode())
			.body(response);
	}
}
