package com.barter.domain.trade.immediatetrade.dto.request;

import com.barter.domain.product.entity.RegisteredProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateImmediateTradeRequestDto {
	RegisteredProduct registeredProduct;
	@NotBlank
	@Size(min = 5)
	String title;
	@NotBlank
	@Size(min = 5)
	String description;
}
