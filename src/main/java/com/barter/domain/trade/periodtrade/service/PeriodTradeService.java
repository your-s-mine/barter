package com.barter.domain.trade.periodtrade.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.trade.periodtrade.PeriodTradeRepository;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.UpdatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.UpdatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeriodTradeService {

	private final PeriodTradeRepository periodTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
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

	// TODO : (PeriodTrade 조회) : 멤버 구현 시 멤버정보를 받아서 해당 멤버가 참여한 PeriodTrade 만 조회 가능하도록 하기

	@Transactional(readOnly = true)
	public PagedModel<FindPeriodTradeResDto> findPeriodTrades(Pageable pageable) {
		Page<FindPeriodTradeResDto> trades = periodTradeRepository.findAll(pageable)
			.map(FindPeriodTradeResDto::from);
		return new PagedModel<>(trades);
	}

	@Transactional(readOnly = true)
	public FindPeriodTradeResDto findPeriodTradeById(Long id) {
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.addViewCount();

		return FindPeriodTradeResDto.from(periodTrade);
	}

	@Transactional
	public UpdatePeriodTradeResDto updatePeriodTrade(Long id, @Valid UpdatePeriodTradeReqDto reqDto) {

		RegisteredProduct product = registeredProductRepository.findById(reqDto.getProductId()).orElseThrow(
			() -> new IllegalArgumentException("해당 맴베의 등록된 상품이 존재하지 않습니다.")
		);

		if (product.getMember().getId() != 1) {// 현재 인증된 멤버 아이디가 1 이라고 일단 가정
			throw new IllegalArgumentException("해당 물품에 대한 수정 권한이 없습니다.");
		}

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.update(reqDto.getTitle(), reqDto.getDescription());

		return UpdatePeriodTradeResDto.from(periodTrade); // save 써도 되고 안써도 되고

	}
}
