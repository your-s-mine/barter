package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class AuthException extends ExpectedException {
	public AuthException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
