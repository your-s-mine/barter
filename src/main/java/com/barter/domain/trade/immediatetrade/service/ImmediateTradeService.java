package com.barter.domain.trade.immediatetrade.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.notification.enums.EventKind;
import com.barter.domain.notification.service.NotificationService;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.entity.SuggestedProduct;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.domain.product.enums.TradeType;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.CreateTradeSuggestProductReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.request.UpdateStatusReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindSuggestForImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.event.trade.TradeNotificationEvent;
import com.barter.exception.customexceptions.ImmediateTradeException;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImmediateTradeService {
	private final ImmediateTradeRepository immediateTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final TradeProductRepository tradeProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;
	private final ApplicationEventPublisher publisher;
	private final NotificationService notificationService;

	@Transactional
	public FindImmediateTradeResDto create(CreateImmediateTradeReqDto reqDto) {
		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProductId()).orElseThrow(
				() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE)
			);

		ImmediateTrade immediateTrade = ImmediateTrade.builder()
			.title(reqDto.getTitle())
			.description(reqDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();

		registeredProduct.changeStatusRegistering();
		ImmediateTrade savedTrade = immediateTradeRepository.save(immediateTrade);
		publisher.publishEvent(TradeNotificationEvent.builder()
			.tradeId(savedTrade.getId())
			.type(TradeType.IMMEDIATE)
			.productName(savedTrade.getProduct().getName())
			.build());
		return FindImmediateTradeResDto.from(savedTrade);
	}

	@Transactional(readOnly = true)
	@CachePut(cacheResolver = "cacheResolver", value = "immediateTradeList", key = "#tradeId")
	public FindImmediateTradeResDto find(Long tradeId) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		return FindImmediateTradeResDto.from(immediateTrade);
	}

	@Transactional
	public void addViewCount(Long tradeId) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.addViewCount();
	}

	@Transactional(readOnly = true)
	public PagedModel<FindImmediateTradeResDto> findImmediateTrades(Pageable pageable) {
		Page<FindImmediateTradeResDto> trades = immediateTradeRepository.findAll(pageable)
			.map(trade -> FindImmediateTradeResDto.from(trade));

		return new PagedModel<>(trades);
	}

	@Transactional
	public FindImmediateTradeResDto update(VerifiedMember member, Long tradeId,
		UpdateImmediateTradeReqDto reqDto) throws
		IllegalAccessException {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());

		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProductId()).orElseThrow(
				() -> new ProductException(ExceptionCode.NOT_FOUND_REGISTERED_PRODUCT)
			);

		immediateTrade.update(reqDto);

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(updatedTrade);
	}

	public String delete(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());
		if (!(immediateTrade.getStatus() == TradeStatus.PENDING)) {
			throw new ImmediateTradeException(ExceptionCode.IT_NOT_PENDING_FOR_DELETION);
		}

		immediateTradeRepository.delete(immediateTrade);

		return "교환 삭제 완료";
	}

	@Transactional
	public String createTradeSuggest(Long tradeId, CreateTradeSuggestProductReqDto reqDto, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateIsSelfSuggest(member.getId());

		if (!immediateTrade.validateTradeStatus(immediateTrade.getStatus())) {
			throw new ImmediateTradeException(ExceptionCode.IT_NOT_PENDING_FOR_SUGGEST);
		}

		List<TradeProduct> tradeProducts = new ArrayList<>();

		for (Long productId : reqDto.getSuggestedProductIds()) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(productId).orElseThrow(
				() -> new ProductException(ExceptionCode.NOT_FOUND_SUGGESTED_PRODUCT));

			if (!suggestedProduct.validateProductStatus(suggestedProduct.getStatus())) {
				throw new ProductException(ExceptionCode.NOT_PENDING_FOR_SUGGEST);
			}

			suggestedProduct.changStatusSuggesting();

			TradeProduct tradeProduct = TradeProduct.builder()
				.tradeId(tradeId)
				.suggestedProduct(suggestedProduct)
				.tradeType(TradeType.IMMEDIATE)
				.build();

			tradeProducts.add(tradeProduct);
		}

		tradeProductRepository.saveAll(tradeProducts);

		// 알림 (교환 등록자에게)
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST, immediateTrade.getProduct().getMember().getId(),
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);

		return "제안 완료";
	}

	@Transactional
	public String acceptTradeSuggest(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());

		immediateTrade.changeStatusInProgress();
		immediateTrade.getProduct().changeStatusAccepted();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changStatusAccepted();
		}

		// 알림 (제안자에게)
		Long suggesterId = tradeProducts.get(0).getSuggestedProduct().getMember().getId();
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_ACCEPT, suggesterId,
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);

		return "제안 수락 완료";
	}

	@Transactional
	public String denyTradeSuggest(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());
		immediateTrade.changeStatusPending();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changeStatusPending();
		}

		tradeProductRepository.deleteAll(tradeProducts);

		// 알림 (제안자에게)
		Long suggesterId = tradeProducts.get(0).getSuggestedProduct().getMember().getId();
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_DENY, suggesterId,
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);

		return "제안 거절";
	}

	@Transactional
	public FindImmediateTradeResDto updateStatusCompleted(Long tradeId, UpdateStatusReqDto reqDto,
		VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());

		if (!(immediateTrade.isInProgress()) || !(reqDto.getTradeStatus() == TradeStatus.COMPLETED)) {
			throw new ImmediateTradeException(ExceptionCode.IT_NOT_IN_PROGRESS_FOR_UPDATING_COMPLETED);
		}

		immediateTrade.changeStatusCompleted();
		immediateTrade.getProduct().changeStatusCompleted();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(tradeId,
			TradeType.IMMEDIATE);
		// 최종 교환 제안자와 선택받지 못한 제안자(들)의 ID 값이 필요해 아래와 같이 수정했습니다.
		Long finalSuggesterId = 0L;
		Set<Long> suggesterIds = new HashSet<>();
		for (TradeProduct tradeProduct : tradeProducts) {
			if (tradeProduct.getSuggestedProduct().getStatus() == SuggestedStatus.ACCEPTED) {
				tradeProduct.getSuggestedProduct().changeStatusCompleted();
				if (finalSuggesterId == 0L) {
					finalSuggesterId = tradeProduct.getSuggestedProduct().getMember().getId();
				}
			}

			if (tradeProduct.getSuggestedProduct().getStatus() == SuggestedStatus.SUGGESTING) {
				suggesterIds.add(tradeProduct.getSuggestedProduct().getMember().getId());
				tradeProduct.getSuggestedProduct().changeStatusPending();
				tradeProductRepository.delete(tradeProduct);
			}
		}

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);

		// 알림 (교환 등록자에게)
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_COMPLETE, immediateTrade.getProduct().getMember().getId(),
			TradeType.IMMEDIATE, updatedTrade.getId(), updatedTrade.getTitle()
		);
		// 알림 (교환에 성공한 제안자에게)
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_COMPLETE, finalSuggesterId,
			TradeType.IMMEDIATE, updatedTrade.getId(), updatedTrade.getTitle()
		);
		// 알림 (교환에 실패한 나머지 제안자(들)에게)
		for (Long suggesterId : suggesterIds) {
			notificationService.saveTradeNotification(
				EventKind.IMMEDIATE_TRADE_SUGGEST_DENY, suggesterId,
				TradeType.IMMEDIATE, updatedTrade.getId(), updatedTrade.getTitle()
			);
		}

		return FindImmediateTradeResDto.from(updatedTrade);
	}

	@Transactional
	public String cancelAcceptanceOfSuggest(Long tradeId, VerifiedMember member) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());

		if (!(immediateTrade.isInProgress())) {
			throw new ImmediateTradeException(ExceptionCode.IT_NOT_IN_PROGRESS_FOR_CANCELING);
		}

		immediateTrade.changeStatusPending();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(tradeId,
			TradeType.IMMEDIATE);
		for (TradeProduct tradeProduct : tradeProducts) {
			tradeProduct.getSuggestedProduct().changeStatusPending();
		}
		tradeProductRepository.deleteAllInBatch(tradeProducts);

		// 알림 (제안자에게)
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_CANCEL, tradeProducts.get(0).getSuggestedProduct().getMember().getId(),
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);

		return "제안 수락 취소 완료";
	}

	public List<FindSuggestForImmediateTradeResDto> findSuggestForImmediateTrade(
		Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new ImmediateTradeException(ExceptionCode.IT_NOT_FOUND_IMMEDIATE_TRADE));

		immediateTrade.validateAuthority(member.getId());

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(
			tradeId, TradeType.IMMEDIATE);

		List<FindSuggestForImmediateTradeResDto> resDtos = new ArrayList<>();

		for (TradeProduct tp : tradeProducts) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(tp.getSuggestedProduct().getId())
				.orElseThrow(() -> new ProductException(ExceptionCode.NOT_FOUND_SUGGESTED_PRODUCT));

			resDtos.add(FindSuggestForImmediateTradeResDto.from(suggestedProduct));
		}
		return resDtos;
	}
}