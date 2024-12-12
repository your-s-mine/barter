package com.barter.event.trade.PeriodTradeEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "기간 교환 마감 이벤트 ")
@Component
@RequiredArgsConstructor
public class PeriodTradeCloseEventListener {

	private final TaskScheduler taskScheduler;
	private final PeriodTradeRepository periodTradeRepository; // 추가된 부분

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void addPeriodTradeCloseEvent(PeriodTradeCloseEvent event) {
		PeriodTrade periodTrade = event.getPeriodTrade();
		updatePeriodTradeClosed(periodTrade);
	}

	private void updatePeriodTradeClosed(PeriodTrade periodTrade) {
		Long periodTradeId = periodTrade.getId();
		Instant closeTime = toInstant(periodTrade.getEndedAt());

		LocalDateTime closeTimeKST = LocalDateTime.ofInstant(closeTime, ZoneId.of("Asia/Seoul"));

		log.info("기간 교환 마감 시간 스케줄링, PeriodTradeId : {} , closeTime : {}", periodTradeId, closeTimeKST);
		taskScheduler.schedule(() ->
			{
				periodTrade.updatePeriodTradeStatus(TradeStatus.CLOSED);
				periodTradeRepository.save(periodTrade);
				log.info("기간 교환 상태가 CLOSED 로 변경되었습니다. PeriodTradeId: {}", periodTradeId);
			},
			closeTime);
	}

	private Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();
	} // 현재 시스템의 기본 시간대로 설정

}
