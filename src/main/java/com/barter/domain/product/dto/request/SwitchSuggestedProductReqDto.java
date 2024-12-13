package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SwitchSuggestedProductReqDto {

	@NotNull
	private Long registeredProductId;
}
