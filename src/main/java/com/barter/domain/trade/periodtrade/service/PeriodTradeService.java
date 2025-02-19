package com.barter.domain.trade.periodtrade.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
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
import com.barter.event.trade.TradeNotificationEvent;
import com.barter.exception.customexceptions.PeriodTradeException;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

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
	private final PeriodTradeCacheService periodTradeCacheService;

	@Transactional
	public CreatePeriodTradeResDto createPeriodTrades(VerifiedMember member, CreatePeriodTradeReqDto reqDto) {

		RegisteredProduct registeredProduct = registeredProductRepository.findById(reqDto.getRegisteredProductId())
			.orElseThrow(
				() -> new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT)
			);

		registeredProduct.validateOwner(member.getId());
		registeredProduct.validatePendingStatusBeforeUpload();

		PeriodTrade periodTrade = PeriodTrade.createInitPeriodTrade(reqDto.getTitle(), reqDto.getDescription(),
			registeredProduct, reqDto.getAddress1(), reqDto.getAddress2(), reqDto.getLongitude(), reqDto.getLatitude(),
			reqDto.getEndedAt());

		registeredProduct.updateStatus(RegisteredStatus.REGISTERING.toString());
		periodTrade.validateIsExceededMaxEndDate();

		PeriodTrade savedPeriodTrade = periodTradeRepository.save(periodTrade);
		// 이벤트 발행
		eventPublisher.publishEvent(new PeriodTradeCloseEvent(periodTrade));
		eventPublisher.publishEvent(TradeNotificationEvent.builder()
			.tradeId(savedPeriodTrade.getId())
			.type(TradeType.PERIOD)
			.productName(savedPeriodTrade.getRegisteredProduct().getName())
			.build());
		return CreatePeriodTradeResDto.from(periodTrade);
	}

	@Transactional(readOnly = true)
	public PagedModel<FindPeriodTradeResDto> findPeriodTrades(Pageable pageable) {
		Page<FindPeriodTradeResDto> trades = periodTradeCacheService.getPeriodTradesFromCache(pageable);
		return new PagedModel<>(trades);
	}

	@Transactional
	public FindPeriodTradeResDto findPeriodTradeById(Long id) {
		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
		);

		periodTradeCacheService.addViewCount(periodTrade);

		return FindPeriodTradeResDto.from(periodTrade);
	}

	@Cacheable(cacheResolver = "cacheResolver", value = "suggestionList")
	public List<FindPeriodTradeSuggestionResDto> findPeriodTradesSuggestion(Long tradeId) {
		return getPeriodTradeSuggestions(tradeId);

	}

	@Transactional
	public UpdatePeriodTradeResDto updatePeriodTrade(VerifiedMember member, Long id, UpdatePeriodTradeReqDto reqDto) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
		);
		periodTrade.validateAuthority(member.getId());
		periodTrade.validateIsCompleted();
		periodTrade.update(reqDto.getTitle(), reqDto.getDescription(), reqDto.getAddress1(), reqDto.getAddress2(),
			reqDto.getLongitude(), reqDto.getLatitude());

		return UpdatePeriodTradeResDto.from(periodTrade);

	}

	@Transactional
	public void deletePeriodTrade(VerifiedMember member, Long id) {

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
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
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
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
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
		);

		periodTrade.validateAuthority(member.getId()); // 교환 (게시글)의 주인만 변경 가능
		periodTrade.validateIsCompleted(); // 이미 완료된 교환 건은 수정 불가
		boolean isStatusUpdatable = periodTrade.updatePeriodTradeStatus(reqDto.getTradeStatus());
		if (!isStatusUpdatable) {
			throw new PeriodTradeException(ExceptionCode.INVALID_PERIOD_TRADE_STATUS_CHANGE);
		}
		List<TradeProduct> allTradeProducts = tradeProductRepository.findTradeProductsWithSuggestedProductByPeriodTradeId(
			TradeType.PERIOD, periodTrade.getId());

		if (reqDto.getTradeStatus().equals(TradeStatus.CLOSED)) {

			// 기존 제안자들의 ID 값이 필요해 위의 코드를 아래와 같이 수정하였습니다.
			Set<Long> suggesterIds = new HashSet<>();
			for (TradeProduct tradeProduct : allTradeProducts) {
				tradeProduct.getSuggestedProduct().changeStatusPending();
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

			// tradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changeStatusPending());
			// 기존 제안자들의 ID 값이 필요해 위의 코드를 아래와 같이 수정하였습니다.
			Set<Long> suggesterIds = new HashSet<>();
			for (TradeProduct tradeProduct : tradeProducts) {
				tradeProduct.getSuggestedProduct().changeStatusPending();
				suggesterIds.add(tradeProduct.getSuggestedProduct().getMember().getId());
			}
			acceptedTradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changeStatusCompleted());
			tradeProductRepository.deleteAll(tradeProducts);

			// 알림 (교환 등록자에게)
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_COMPLETE, periodTrade.getRegisteredProduct().getMember().getId(),
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
			// 알림 (교환에 성공한 제안자에게)
			Long finalSuggesterId = acceptedTradeProducts.get(0).getSuggestedProduct().getMember().getId();
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_COMPLETE, finalSuggesterId,
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
			// 알림 (기존 제안자들에게)
			for (Long suggesterId : suggesterIds) {
				notificationService.saveTradeNotification(
					EventKind.PERIOD_TRADE_SUGGEST_DENY, suggesterId,
					TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
				);
			}
		}

		return StatusUpdateResDto.from(periodTrade);
	}

	@Transactional
	public AcceptPeriodTradeResDto acceptPeriodTrade(VerifiedMember member, Long id, AcceptPeriodTradeReqDto reqDto) {

		if (member.getId().equals(reqDto.getSuggestedMemberId())) {
			throw new PeriodTradeException(ExceptionCode.INVALID_SELF_ACCEPT);
		}

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
		);
		periodTrade.validateAuthority(member.getId());

		periodTrade.validateIsCompleted();
		periodTrade.validateIsClosed();

		// 해당하는 교환 id, 교환 타입 에 맞게 제안된 물품들을 조회
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
				tradeProductRepository.delete(tradeProduct);
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
			throw new PeriodTradeException(ExceptionCode.INVALID_SELF_DENY);
		}

		PeriodTrade periodTrade = periodTradeRepository.findById(id).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
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
					.updateStatus(RegisteredStatus.REGISTERING.toString());
				tradeProductRepository.delete(tradeProduct);
			}

		}

		// 알림 (제안자에게 알림)
		notificationService.saveTradeNotification(
			EventKind.PERIOD_TRADE_SUGGEST_DENY, reqDto.getSuggestedMemberId(),
			TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
		);

		return DenyPeriodTradeResDto.from(periodTrade);
	}

	@Transactional
	public void closePeriodTrade(Long periodTradeId) {
		PeriodTrade periodTrade = periodTradeRepository.findById(periodTradeId).orElseThrow(
			() -> new PeriodTradeException(ExceptionCode.NOT_FOUND_PERIOD_TRADE)
		);
		periodTrade.updatePeriodTradeStatus(TradeStatus.CLOSED);
		List<TradeProduct> allTradeProducts = tradeProductRepository.findTradeProductsWithSuggestedProductByPeriodTradeId(
			TradeType.PERIOD, periodTrade.getId());
		tradeProductRepository.deleteAll(allTradeProducts);

		// allTradeProducts.forEach(tradeProduct -> tradeProduct.getSuggestedProduct().changeStatusPending());
		// 해당 기간 교환에 제안한 제안자들의 ID 정보를 얻기 위해 위의 코드를 아래와 같이 수정했습니다.
		Set<Long> suggesterIds = new HashSet<>();
		for (TradeProduct tradeProduct : allTradeProducts) {
			tradeProduct.getSuggestedProduct().changeStatusPending();
			suggesterIds.add(tradeProduct.getSuggestedProduct().getMember().getId());
		}

		// 알림 (교환 등록자에게)
		notificationService.saveTradeNotification(
			EventKind.PERIOD_TRADE_PERIOD_EXPIRES, periodTrade.getRegisteredProduct().getMember().getId(),
			TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
		);
		// 알림 (제안자(들)에게)
		for (Long suggesterId : suggesterIds) {
			notificationService.saveTradeNotification(
				EventKind.PERIOD_TRADE_SUGGEST_DENY, suggesterId,
				TradeType.PERIOD, periodTrade.getId(), periodTrade.getTitle()
			);
		}
	}

	private List<SuggestedProduct> findSuggestedProductByIds(List<Long> productIds, Long memberId) {
		return productIds.stream()
			.map(id -> {
					SuggestedProduct product = suggestedProductRepository.findById(id)
						.orElseThrow(() -> new ProductException(ExceptionCode.NOT_FOUND_SUGGESTED_PRODUCT));
					if (!product.getStatus().equals(SuggestedStatus.PENDING)) {
						throw new PeriodTradeException(ExceptionCode.ALREADY_SUGGESTED_PRODUCT);
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
