package com.barter.exception.customexceptions;

import com.barter.exception.ExpectedException;
import com.barter.exception.enums.ExceptionCode;

public class FavoriteKeywordException extends ExpectedException {
	public FavoriteKeywordException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
