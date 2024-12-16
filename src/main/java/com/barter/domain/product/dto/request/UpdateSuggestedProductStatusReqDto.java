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
public class UpdateSuggestedProductStatusReqDto {

	@NotNull
	private Long id;

	@NotNull
	private String status;
}
