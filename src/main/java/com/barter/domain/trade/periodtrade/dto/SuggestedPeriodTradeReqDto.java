package com.barter.domain.trade.periodtrade.dto;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SuggestedPeriodTradeReqDto {
	@Size(min = 1, max = 3, message = "최소1개, 최대 3개의 물품을 제안 가능합니다.")
	private List<Long> productIds;
}
