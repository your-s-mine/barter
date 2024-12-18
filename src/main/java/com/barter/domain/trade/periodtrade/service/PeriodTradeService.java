package com.barter.domain.trade.periodtrade.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.product.service.ProductSwitchService;
import com.barter.domain.trade.periodtrade.dto.request.AcceptPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.CreatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.DenyPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.StatusUpdateReqDto;
import com.barter.domain.trade.periodtrade.dto.request.SuggestedPeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.request.UpdatePeriodTradeReqDto;
import com.barter.domain.trade.periodtrade.dto.response.AcceptPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.CreatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.DenyPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.StatusUpdateResDto;
import com.barter.domain.trade.periodtrade.dto.response.SuggestedPeriodTradeResDto;
import com.barter.domain.trade.periodtrade.dto.response.UpdatePeriodTradeResDto;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.repository.PeriodTradeRepository;
import com.barter.event.trade.PeriodTradeEvent.PeriodTradeCloseEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodTradeService {

	private final PeriodTradeRepository periodTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;
	private final TradeProductRepository tradeProductRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final ProductSwitchService productSwitchService;

	// TODO : Member 는 나중에 VerifiedMember 로 변경될 예정
	@Transactional
	public CreatePeriodTradeResDto createPeriodTrades(VerifiedMember member, CreatePeriodTradeReqDto reqDto) {

		RegisteredProduct registeredProduct = registeredProductRepository.findById(reqDto.getRegisteredProductId())
			.orElseThrow(
				() -> new IllegalArgumentException("없는 등록된 물건입니다.")
			);

		registeredProduct.validateOwner(member.getId());
		registeredProduct.validatePendingStatusBeforeUpload();

		PeriodTrade periodTrade = PeriodTrade.createInitPeriodTrade(reqDto.getTitle(), reqDto.getDescription(),
			registeredProduct,
			reqDto.getEndedAt());

		registeredProduct.updateStatus(RegisteredStatus.REGISTERING.toString());
		periodTrade.validateIsExceededMaxEndDate();

		periodTradeRepository.save(periodTrade);
		// 이벤트 발행
		eventPublisher.publishEvent(new PeriodTradeCloseEvent(periodTrade));

		return CreatePeriodTradeResDto.from(periodTrade);
	}

	@Transactional(readOnly = true)
	public PagedModel<FindPeriodTradeResDto> findPeriodTrades(Pageable pageable) {
		Page<FindPeriodTradeResDto> trades = periodTradeRepository.findAll(pageable)
			.map(FindPeriodTradeResDto::from);
		return new PagedModel<>(trades);
	}

	@Transactional
	public FindPeriodTradeResDto findPeriodTradeById(Long id) {
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.addViewCount();

		return FindPeriodTradeResDto.from(periodTrade);
	}

	@Transactional
	public UpdatePeriodTradeResDto updatePeriodTrade(VerifiedMember member, Long id, UpdatePeriodTradeReqDto reqDto) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(member.getId());
		periodTrade.validateIsCompleted();
		periodTrade.update(reqDto.getTitle(), reqDto.getDescription());

		return UpdatePeriodTradeResDto.from(periodTrade);

	}

	@Transactional
	public void deletePeriodTrade(VerifiedMember member, Long id) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.updateRegisteredProduct(RegisteredStatus.PENDING);

		periodTrade.validateAuthority(member.getId());
		periodTrade.validateIsCompleted();
		periodTradeRepository.delete(periodTrade);

	}

	@Transactional
	public SuggestedPeriodTradeResDto suggestPeriodTrade(VerifiedMember member, Long id,
		SuggestedPeriodTradeReqDto reqDto) {

		// 알림 추가

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.validateSuggestAuthority(member.getId()); // 자신의 교환 (게시글) 에 제안 불가

		// 기간 교환 시작 전(PENDING) 또는 이미 거래된(COMPLETED) 교환인 경우
		periodTrade.validateIsPending();
		periodTrade.validateIsCompleted();

		List<SuggestedProduct> suggestedProduct = findSuggestedProductByIds(reqDto.getProductIds());

		List<TradeProduct> tradeProducts = suggestedProduct.stream()
			.map(product -> {
				TradeProduct tradeProduct = TradeProduct.createTradeProduct(id, TradeType.PERIOD, product);
				product.changStatusSuggesting();
				return tradeProduct;
			}).toList();

		tradeProductRepository.saveAll(tradeProducts);

		return SuggestedPeriodTradeResDto.from(id, suggestedProduct);

	}

	@Transactional
	public StatusUpdateResDto updatePeriodTradeStatus(VerifiedMember member, Long id, StatusUpdateReqDto reqDto) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.validateAuthority(member.getId()); // 교환 (게시글)의 주인만 변경 가능
		periodTrade.validateIsCompleted(); // 이미 완료된 교환 건은 수정 불가
		boolean isStatusUpdatable = periodTrade.updatePeriodTradeStatus(reqDto.getTradeStatus());
		if (!isStatusUpdatable) {
			throw new IllegalArgumentException("불가능한 상태 변경 입니다.");
		}

		// 알림 추가

		return StatusUpdateResDto.from(periodTrade);
	}

	@Transactional
	public AcceptPeriodTradeResDto acceptPeriodTrade(VerifiedMember member, Long id, AcceptPeriodTradeReqDto reqDto) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(member.getId());
		periodTrade.validateInProgress();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(id, TradeType.PERIOD);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();

			if (suggestedProduct.getMember().getId().equals(reqDto.getMemberId()) && suggestedProduct.getStatus()
				.equals(SuggestedStatus.SUGGESTING)) {
				suggestedProduct.changStatusAccepted();
				periodTrade.getRegisteredProduct()
					.updateStatus(RegisteredStatus.ACCEPTED.toString());// enum 타입이 아니어도 검증 로직이 구현되어 있기 때문에 일단 이렇게 구현함
			}

			// 한 교환에 대해서 여러번의 교환은 불가능 (회의 때 말한 같은 물건으로 여러번 다른 교환 시도 방지 위함)

		}
		// 알림 (제안자에게 알림)

		periodTrade.updatePeriodTradeStatusCompleted();

		return AcceptPeriodTradeResDto.from(periodTrade);

	}

	// TODO : Deny 부분이 Accept 랑 구조가 비슷해서 단순화 시킬 필요 있다.
	@Transactional
	public DenyPeriodTradeResDto denyPeriodTrade(VerifiedMember member, Long id, DenyPeriodTradeReqDto reqDto) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(member.getId());
		periodTrade.validateInProgress();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(id, TradeType.PERIOD);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();

			if (suggestedProduct.getMember().getId().equals(reqDto.getMemberId()) && suggestedProduct.getStatus()
				.equals(SuggestedStatus.SUGGESTING)) {
				suggestedProduct.changStatusPending();
				periodTrade.getRegisteredProduct()
					.updateStatus(RegisteredStatus.PENDING.toString());
			}

		}
		// 알림 (제안자에게 알림)
		return DenyPeriodTradeResDto.from(periodTrade);
	}

	private List<SuggestedProduct> findSuggestedProductByIds(List<Long> productIds) {
		return productIds.stream()
			.map(id -> {
					SuggestedProduct product = suggestedProductRepository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("해당 id 의 등록된 제품이 없습니다."));
					if (!product.getStatus().equals(SuggestedStatus.PENDING)) {
						throw new IllegalArgumentException("다른 교환에 제안된 상품은 제안 할 수 없습니다.");
					}

					return product;
				}
			)
			.toList();
	}
}
