package com.barter.domain.product.enums;

import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

public enum SuggestedStatus {
	PENDING, SUGGESTING, ACCEPTED, COMPLETED;

	public static SuggestedStatus findSuggestedStatus(String status) {
		for (SuggestedStatus suggestedStatus : SuggestedStatus.values()) {
			if (suggestedStatus.name().equals(status)) {
				return suggestedStatus;
			}
		}

		throw new ProductException(ExceptionCode.NOT_SUPPORT_SUGGESTED_STATUS);
	}
}
