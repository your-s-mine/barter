package com.barter.domain.product.enums;

import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

public enum RegisteredStatus {
	PENDING, REGISTERING, ACCEPTED, COMPLETED;

	public static RegisteredStatus findRegisteredStatus(String status) {
		for (RegisteredStatus registeredStatus : RegisteredStatus.values()) {
			if (registeredStatus.name().equalsIgnoreCase(status)) {
				return registeredStatus;
			}
		}

		throw new ProductException(ExceptionCode.NOT_SUPPORT_REGISTERED_STATUS);
	}
}
