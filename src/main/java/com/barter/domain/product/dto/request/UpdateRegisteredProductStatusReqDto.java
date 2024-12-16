package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegisteredProductStatusReqDto {

	@NotNull
	private Long id;

	@NotNull
	private String status;
}
