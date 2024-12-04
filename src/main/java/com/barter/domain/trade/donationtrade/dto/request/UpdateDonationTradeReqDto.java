package com.barter.domain.trade.donationtrade.dto.request;

import lombok.Getter;

@Getter
public class UpdateDonationTradeReqDto {

	private String title;
	private String description;
	private Long productId;
}
