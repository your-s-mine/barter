package com.barter.domain.product.enums;

public enum SuggestedStatus {
	PENDING, SUGGESTING, ACCEPTED;

	public static SuggestedStatus findSuggestedStatus(String status) {
		for (SuggestedStatus suggestedStatus : SuggestedStatus.values()) {
			if (suggestedStatus.name().equals(status)) {
				return suggestedStatus;
			}
		}

		throw new IllegalArgumentException("Suggested status has not value " + status);
	}
}
