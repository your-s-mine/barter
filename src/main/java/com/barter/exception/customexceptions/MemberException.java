package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class MemberException extends ExpectedException {
	public MemberException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
