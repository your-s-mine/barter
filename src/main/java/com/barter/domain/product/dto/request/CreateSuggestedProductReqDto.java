package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSuggestedProductReqDto {

	@NotNull(message = "제안 물품의 이름을 반드시 작성해주세요!")
	@Size(min = 5)
	private String name;

	@NotNull(message = "제안 물품의 이름을 반드시 작성해주세요!")
	@Size(min = 5)
	private String description;
}
