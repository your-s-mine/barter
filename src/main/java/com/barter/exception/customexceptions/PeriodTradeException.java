package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class PeriodTradeException extends ExpectedException {
	public PeriodTradeException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
