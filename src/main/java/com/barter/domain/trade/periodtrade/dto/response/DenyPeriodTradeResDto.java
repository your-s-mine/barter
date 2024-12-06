package com.barter.domain.trade.periodtrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DenyPeriodTradeResDto {
	private Long periodTradeId;
	private LocalDateTime deniedAt;
	private TradeStatus tradeStatus;

	@Builder
	public DenyPeriodTradeResDto(Long periodTradeId, TradeStatus tradeStatus) {
		this.periodTradeId = periodTradeId;
		this.deniedAt = LocalDateTime.now();
		this.tradeStatus = tradeStatus;
	}

	public static DenyPeriodTradeResDto from(PeriodTrade periodTrade) {
		return DenyPeriodTradeResDto
			.builder()
			.periodTradeId(periodTrade.getId())
			.tradeStatus(periodTrade.getStatus())
			.build();

	}
}
