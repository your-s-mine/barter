package com.barter.domain.product.dto.request;

import java.util.List;

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

	@NotNull
	private List<String> deleteImageNames;
}
