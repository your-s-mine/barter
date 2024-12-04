package com.barter.domain.trade.donationtrade.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateDonationTradeReqDto {
	private Long productId;
	private Integer maxAmount;
	@NotBlank
	@Size(min = 5)
	private String title;
	@NotBlank
	@Size(min = 5)
	private String description;
	private LocalDateTime endedAt;
}
