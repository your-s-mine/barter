package com.barter.domain.trade.donationtrade.dto.response;

import com.barter.domain.trade.enums.DonationResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuggestDonationTradeResDto {

	private String message;
	private DonationResult result;
}
