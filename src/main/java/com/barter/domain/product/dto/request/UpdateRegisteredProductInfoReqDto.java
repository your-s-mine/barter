package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateRegisteredProductInfoReqDto {

	@NotNull
	private Long id;

	@NotNull
	@Size(min = 5)
	private String name;

	@NotNull
	@Size(min = 5)
	private String description;
}
