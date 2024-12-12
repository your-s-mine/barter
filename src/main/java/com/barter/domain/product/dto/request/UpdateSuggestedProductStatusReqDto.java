package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateSuggestedProductStatusReqDto {

	@NotNull
	private Long id;

	@NotNull
	private String status;
}
