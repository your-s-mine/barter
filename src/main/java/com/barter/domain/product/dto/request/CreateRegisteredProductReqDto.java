package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRegisteredProductReqDto {

	@NotBlank(message = "제안 물품의 이름을 반드시 작성해주세요!")
	@Size(min = 5)
	private String name;

	@NotBlank(message = "제안 물품의 설명을 반드시 작성해주세요!")
	@Size(min = 5)
	private String description;
}
