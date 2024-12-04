package com.barter.domain.trade.donationtrade.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateDonationTradeReqDto {

	private Long productId;

	@Min(value = 1, message = "최대 수량은 한 개 이상만 가능합니다.")
	private Integer maxAmount;

	@NotBlank
	@Size(min = 5, message = "제목은 5글자 이상만 가능합니다.")
	private String title;

	@NotBlank
	@Size(min = 5, message = "설명은 5글자 이상만 가능합니다.")
	private String description;
	private LocalDateTime endedAt;
}
