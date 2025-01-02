package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class SearchException extends ExpectedException {
	public SearchException(ExceptionCode exceptionCode) {
      super(exceptionCode);
    }
}
