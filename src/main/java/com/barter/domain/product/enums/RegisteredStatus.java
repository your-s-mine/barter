package com.barter.domain.product.enums;

public enum RegisteredStatus {
	PENDING, REGISTERING, ACCEPTED;

	public static RegisteredStatus findRegisteredStatus(String status) {
		for (RegisteredStatus registeredStatus : RegisteredStatus.values()) {
			if (registeredStatus.name().equalsIgnoreCase(status)) {
				return registeredStatus;
			}
		}

		throw new IllegalArgumentException("Registered status has not value " + status);
	}
}
