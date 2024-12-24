package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class DonationTradeException extends ExpectedException {
	public DonationTradeException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
