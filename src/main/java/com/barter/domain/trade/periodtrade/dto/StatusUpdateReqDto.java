package com.barter.domain.trade.periodtrade.dto;

import com.barter.domain.trade.enums.TradeStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StatusUpdateReqDto {
	@NotNull(message = "상태 값은 필수 입니다.")
	private TradeStatus tradeStatus;
}
