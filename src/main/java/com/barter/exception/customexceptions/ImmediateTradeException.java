package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class ImmediateTradeException extends ExpectedException {
	public ImmediateTradeException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}