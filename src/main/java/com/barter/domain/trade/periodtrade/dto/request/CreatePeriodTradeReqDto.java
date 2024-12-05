package com.barter.domain.trade.periodtrade.dto.request;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePeriodTradeReqDto {

	@NotBlank(message = "타이틀은 필수입니다.")
	@Size(min = 5, message = "타이틀은 5글자 이상이어야 합니다.")
	private String title;
	@NotBlank(message = "설명은 필수입니다.")
	@Size(min = 5, message = "설명은 5글자 이상이어야 합니다.")
	private String description;
	@NotNull(message = "상품은 필수입니다.")
	private RegisteredProduct product;
	@NotNull(message = "마감일은 필수입니다.")
	private LocalDateTime endedAt;
}
