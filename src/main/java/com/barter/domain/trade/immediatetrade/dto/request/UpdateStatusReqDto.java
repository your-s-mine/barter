package com.barter.domain.trade.immediatetrade.dto.request;

import com.barter.domain.trade.enums.TradeStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateStatusReqDto {

	TradeStatus tradeStatus;
}