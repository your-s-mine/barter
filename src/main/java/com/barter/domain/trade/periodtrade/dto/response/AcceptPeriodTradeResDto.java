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
public class AcceptPeriodTradeResDto {
	private Long periodTradeId;
	private LocalDateTime acceptedAt;
	private TradeStatus tradeStatus;

	@Builder
	public AcceptPeriodTradeResDto(Long periodTradeId, TradeStatus tradeStatus) {
		this.periodTradeId = periodTradeId;
		this.acceptedAt = LocalDateTime.now();
		this.tradeStatus = tradeStatus;
	}

	public static AcceptPeriodTradeResDto from(PeriodTrade periodTrade) {
		return AcceptPeriodTradeResDto
			.builder()
			.periodTradeId(periodTrade.getId())
			.tradeStatus(periodTrade.getStatus())
			.build();
	}
}
