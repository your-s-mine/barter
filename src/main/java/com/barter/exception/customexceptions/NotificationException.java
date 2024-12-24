package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class NotificationException extends ExpectedException {

	public NotificationException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
