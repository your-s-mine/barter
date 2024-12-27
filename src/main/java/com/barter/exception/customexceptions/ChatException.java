package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class ChatException extends ExpectedException {
	public ChatException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
