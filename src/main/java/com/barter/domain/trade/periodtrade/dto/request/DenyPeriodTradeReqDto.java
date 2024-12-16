package com.barter.domain.trade.periodtrade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // for test
public class DenyPeriodTradeReqDto {
	private Long memberId;
	//Restful 하게 uri 하려면 pathVariable 보다는 이런식으로 넣는게 좋다고 생각했습니다.
}
