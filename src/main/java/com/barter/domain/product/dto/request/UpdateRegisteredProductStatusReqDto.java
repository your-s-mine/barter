package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateRegisteredProductStatusReqDto {

	@NotNull
	private Long id;

	@NotNull
	private String status;
}
