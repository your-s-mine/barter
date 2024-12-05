package com.barter.domain.trade.periodtrade.dto;

import java.time.LocalDateTime;

import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusUpdateResDto {
	private Long periodTradeId;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;

	@Builder
	public StatusUpdateResDto(Long periodTradeId, LocalDateTime updatedAt, TradeStatus tradeStatus) {
		this.periodTradeId = periodTradeId;
		this.updatedAt = LocalDateTime.now();
		this.tradeStatus = tradeStatus;
	}

	public static StatusUpdateResDto from(PeriodTrade periodTrade) {
		return StatusUpdateResDto.builder()
			.periodTradeId(periodTrade.getId())
			.tradeStatus(periodTrade.getStatus())
			.build();
	}
}
