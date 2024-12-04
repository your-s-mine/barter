package com.barter.domain.trade.periodtrade.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.trade.periodtrade.PeriodTradeRepository;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeriodTradeService {

	private final PeriodTradeRepository periodTradeRepository;
	// private final RegisteredProductRepository registeredProductRepository;
	private final MemberRepository memberRepository;

	public CreatePeriodTradeResDto createPeriodTrades(CreatePeriodTradeReqDto reqDto) {

		/* TODO : RegisteredProduct 가 해당 유저의 물건인지 확인하는 로직 필요
		    해당 로직 추가시 아래 코드는 변경 될 수 있음*/

		PeriodTrade periodTrade = PeriodTrade.createInitPeriodTrade(reqDto.getTitle(), reqDto.getDescription(),
			reqDto.getProduct(),
			reqDto.getEndedAt());

		periodTrade.validateIsExceededMaxEndDate();

		return CreatePeriodTradeResDto.from(periodTradeRepository.save(periodTrade));
	}

	public List<FindPeriodTradeResDto> findPeriodTrades() {
		return FindPeriodTradeResDto.from(periodTradeRepository.findAll());
	}

	public FindPeriodTradeResDto findPeriodTradeById(Long id) {
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		return FindPeriodTradeResDto.from(periodTrade);
	}
}
