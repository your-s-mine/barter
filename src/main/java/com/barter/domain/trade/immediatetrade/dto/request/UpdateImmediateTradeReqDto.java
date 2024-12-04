package com.barter.domain.trade.immediatetrade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateImmediateTradeReqDto {
	private Long registeredProductId;
	@NotBlank
	@Size(min = 5, message = "제목은 5글자 이상만 가능합니다.")
	String title;
	@NotBlank
	@Size(min = 5, message = "설명은 5글자 이상만 가능합니다.")
	String description;
}
