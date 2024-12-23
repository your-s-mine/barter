package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class ProductException extends ExpectedException {

	public ProductException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
