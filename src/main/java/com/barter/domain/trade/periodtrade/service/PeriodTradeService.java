package com.barter.domain.trade.periodtrade.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.dto.response.FindSuggestedProductResDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
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
import com.barter.domain.trade.periodtrade.dto.response.FindPeriodTradeSuggestionResDto;
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
	private final NotificationService notificationService;

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

	public List<FindPeriodTradeSuggestionResDto> findPeriodTradesSuggestion(Long tradeId) {
		return getPeriodTradeSuggestions(tradeId);

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

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);

		periodTrade.validateSuggestAuthority(member.getId()); // 자신의 교환 (게시글) 에 제안 불가

		// PENDING 인 경우와 IN_PROGRESS 의 경우만 제안 가능하다.

		periodTrade.validateIsCompleted();
		periodTrade.validateIsClosed();

		List<SuggestedProduct> suggestedProduct = findSuggestedProductByIds(reqDto.getProductIds(), member.getId());

		List<TradeProduct> tradeProducts = suggestedProduct.stream()
			.map(product -> {
				TradeProduct tradeProduct = TradeProduct.createTradeProduct(id, TradeType.PERIOD, product);
				product.changStatusSuggesting();
				return tradeProduct;
			}).toList();

		tradeProductRepository.saveAll(tradeProducts);

		// 제안신청 알림 저장 및 전달
		notificationService.saveTradeNotification(
			EventKind.PERIOD_TRADE_SUGGEST, periodTrade.getRegisteredProduct().getMember().getId(),
			TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
		);

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
		List<TradeProduct> allTradeProducts = tradeProductRepository.findTradeProductsWithSuggestedProductByPeriodTradeId(
			TradeType.PERIOD, periodTrade.getId());

		if (reqDto.getTradeStatus().equals(TradeStatus.CLOSED)) {

			// allTradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changStatusPending());
			// 기존 제안자들의 ID 값이 필요해 위의 코드를 아래와 같이 수정하였습니다.
			Set<Long> suggesterIds = new HashSet<>();
			for (TradeProduct tradeProduct : allTradeProducts) {
				tradeProduct.getSuggestedProduct().changStatusSuggesting();
				suggesterIds.add(tradeProduct.getSuggestedProduct().getMember().getId());
			}
			tradeProductRepository.deleteAll(allTradeProducts);

			// 알림 (제안신청 했던 제안자들에게)
			for (Long suggesterId : suggesterIds) {
				notificationService.saveTradeNotification(
					EventKind.PERIOD_TRADE_CLOSE, suggesterId,
					TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
				);
			}
		}

		if (reqDto.getTradeStatus().equals(TradeStatus.COMPLETED)) {

			// ACCEPTED 경우 제외하고 조회하여 삭제
			List<TradeProduct> tradeProducts = tradeProductRepository.findTradeProductsByTradeTypeAndTradeIdAndNotSuggestedStatus(
				TradeType.PERIOD, periodTrade.getId(), SuggestedStatus.ACCEPTED);

			List<TradeProduct> acceptedTradeProducts = tradeProductRepository.findTradeProductsByTradeTypeAndTradeIdAndSuggestedStatus(
				TradeType.PERIOD, periodTrade.getId(), SuggestedStatus.ACCEPTED);

			tradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changeStatusPending());
			acceptedTradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changeStatusCompleted());
			tradeProductRepository.deleteAll(allTradeProducts);

			// 알림 (교환 등록자에게)
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_COMPLETE, periodTrade.getRegisteredProduct().getMember().getId(),
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
			// 알림 (교환 제안자에게)
			Long finalSuggesterId = acceptedTradeProducts.get(0).getSuggestedProduct().getMember().getId();
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_COMPLETE, finalSuggesterId,
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
		}

		return StatusUpdateResDto.from(periodTrade);
	}

	@Transactional
	public AcceptPeriodTradeResDto acceptPeriodTrade(VerifiedMember member, Long id, AcceptPeriodTradeReqDto reqDto) {

		if (member.getId().equals(reqDto.getSuggestedMemberId())) {
			throw new IllegalArgumentException("자기 자신을 수락할 수는 없습니다.");
		}

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(member.getId());

		// 아래 두 줄은 없어도 될 것 같긴 하다.
		periodTrade.validateIsCompleted();
		periodTrade.validateIsClosed();

		// 해당하는 교환 id, 교환 타입 에 맞게 제안된 물품들을 조회 -> 추후 삭제 필요 (교환 완료, 만료 시)
		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(id, TradeType.PERIOD);

		Long canceledMemberId = 0L;
		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();

			// 기존 수락된 제안이 있다면 이를 취소 (PENDING으로 상태 변경)
			if (suggestedProduct.getStatus().equals(SuggestedStatus.ACCEPTED)) {
				if (canceledMemberId == 0L) {
					canceledMemberId = suggestedProduct.getMember().getId();
				}
				suggestedProduct.changeStatusPending(); // 기존 수락 상태를 PENDING으로 변경
			}

			if (suggestedProduct.getMember().getId().equals(reqDto.getSuggestedMemberId())
				&& suggestedProduct.getStatus()
				.equals(SuggestedStatus.SUGGESTING)) {
				suggestedProduct.changStatusAccepted();
				periodTrade.getRegisteredProduct()
					.updateStatus(RegisteredStatus.ACCEPTED.toString());
			}

		}

		periodTrade.updatePeriodTradeStatusInProgress();

		// 알림 (제안 수락된 회원에게)
		notificationService.saveTradeNotification(
			EventKind.PERIOD_TRADE_SUGGEST_ACCEPT, reqDto.getSuggestedMemberId(),
			TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
		);
		// 알림 (제안이 취소된 회원이 존재한다면, 제안 취소된 회원에게)
		if (canceledMemberId != 0L) {
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_SUGGEST_DENY, canceledMemberId,
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
		}

		return AcceptPeriodTradeResDto.from(periodTrade);

	}
	// 이미 수락한 제안에 대해서도 거절 할 수 있도록 수정
	// 수락을 여러번 하지 못하도록 함 (다른 수락을 하면 기존 수락은 삭제)

	@Transactional
	public DenyPeriodTradeResDto denyPeriodTrade(VerifiedMember member, Long id, DenyPeriodTradeReqDto reqDto) {

		if (member.getId().equals(reqDto.getSuggestedMemberId())) {
			throw new IllegalArgumentException("자기 자신을 거절할 수는 없습니다.");
		}

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당하는 기간 거래를 찾을 수 없습니다.")
		);
		periodTrade.validateAuthority(member.getId());

		periodTrade.validateIsCompleted();
		periodTrade.validateIsClosed();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(id, TradeType.PERIOD);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();

			if (suggestedProduct.getMember().getId().equals(reqDto.getSuggestedMemberId())
				&& !suggestedProduct.getStatus()
				.equals(SuggestedStatus.PENDING)) {
				suggestedProduct.changeStatusPending();
				periodTrade.getRegisteredProduct()
					.updateStatus(RegisteredStatus.PENDING.toString());
			}

		}

		// 알림 (제안자에게 알림)
		notificationService.saveTradeNotification(
			EventKind.PERIOD_TRADE_SUGGEST_DENY, reqDto.getSuggestedMemberId(),
			TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
		);

		return DenyPeriodTradeResDto.from(periodTrade);
	}

	private List<SuggestedProduct> findSuggestedProductByIds(List<Long> productIds, Long memberId) {
		return productIds.stream()
			.map(id -> {
					SuggestedProduct product = suggestedProductRepository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("해당 id 의 등록된 제품이 없습니다."));
					if (!product.getStatus().equals(SuggestedStatus.PENDING)) {
						throw new IllegalArgumentException("다른 교환에 제안된 상품은 제안 할 수 없습니다.");
					}
					product.checkPermission(memberId); // 자신의 물건만 등록 가능

					return product;
				}
			)
			.toList();
	}

	private List<FindPeriodTradeSuggestionResDto> getPeriodTradeSuggestions(Long tradeId) {
		// 상태가 제공되지 않으면 ACCEPTED 상태로 처리
		List<SuggestedProduct> suggestedProducts;

		suggestedProducts = suggestedProductRepository.findSuggestedProductsByTradeTypeAndTradeId(
			TradeType.PERIOD, tradeId);

		// memberId 기준으로 그룹화
		Map<Long, List<FindSuggestedProductResDto>> productsByMember = suggestedProducts.stream()
			.collect(Collectors.groupingBy(
				suggestedProduct -> suggestedProduct.getMember().getId(),  // memberId 기준으로 그룹화
				Collectors.mapping(FindSuggestedProductResDto::from, Collectors.toList())
				// FindSuggestedProductResDto로 변환
			));

		// 각 memberId에 대해 FindPeriodTradeSuggestionResDto 생성
		return productsByMember.entrySet().stream()
			.map(entry -> FindPeriodTradeSuggestionResDto.from(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
	}
}
