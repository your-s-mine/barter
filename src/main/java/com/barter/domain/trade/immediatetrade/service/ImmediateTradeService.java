package com.barter.domain.trade.immediatetrade.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImmediateTradeService {
	private final ImmediateTradeRepository immediateTradeRepository;
	private final RegisteredProductRepository registeredProductRepository;
	private final TradeProductRepository tradeProductRepository;
	private final SuggestedProductRepository suggestedProductRepository;
	private final NotificationService notificationService;

	@Transactional
	public FindImmediateTradeResDto create(CreateImmediateTradeReqDto reqDto) {
		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProduct().getId()).orElseThrow(
				() -> new IllegalArgumentException("등록 물품을 찾을 수 없습니다.")
			);

		ImmediateTrade immediateTrade = ImmediateTrade.builder()
			.title(reqDto.getTitle())
			.description(reqDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();

		registeredProduct.changStatusRegistering();
		ImmediateTrade savedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(savedTrade);
	}

	@Transactional
	public FindImmediateTradeResDto find(Long tradeId) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.addViewCount();

		return FindImmediateTradeResDto.from(immediateTrade);
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
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		RegisteredProduct registeredProduct = registeredProductRepository
			.findById(reqDto.getRegisteredProductId()).orElseThrow(
				() -> new IllegalArgumentException("등록 물품을 찾을 수 없습니다.")
			);

		immediateTrade.update(reqDto);

		ImmediateTrade updatedTrade = immediateTradeRepository.save(immediateTrade);
		return FindImmediateTradeResDto.from(updatedTrade);
	}

	public String delete(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());
		if (!(immediateTrade.getStatus() == TradeStatus.PENDING)) {
			throw new IllegalStateException("PENDING 상태의 교환만 삭제할 수 있습니다.");
		}

		immediateTradeRepository.delete(immediateTrade);

		return "교환 삭제 완료";
	}

	@Transactional
	public String createTradeSuggest(Long tradeId, CreateTradeSuggestProductReqDto reqDto, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateIsSelfSuggest(member.getId());

		if (!immediateTrade.validateTradeStatus(immediateTrade.getStatus())) {
			throw new IllegalStateException("PENDING 상태의 교환에만 제안할 수 있습니다.");
		}

		List<TradeProduct> tradeProducts = new ArrayList<>();

		for (Long productId : reqDto.getSuggestedProductIds()) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(productId).orElseThrow(
				() -> new IllegalArgumentException("제안 상품을 찾을 수 없습니다.")
			);

			if (!suggestedProduct.validateProductStatus(suggestedProduct.getStatus())) {
				throw new IllegalArgumentException("PENDING 상태의 상품으로만 제안하실 수 있습니다.");
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

		// 이벤트 저장 및 전달
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST, immediateTrade.getProduct().getMember().getId(),
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);
		return "제안 완료";
	}

	@Transactional
	public String acceptTradeSuggest(Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		immediateTrade.changeStatusInProgress();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changStatusAccepted();
		}

		// 이벤트 저장 및 전달
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
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());
		immediateTrade.changeStatusPending();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeId(tradeId);

		for (TradeProduct tradeProduct : tradeProducts) {
			SuggestedProduct suggestedProduct = tradeProduct.getSuggestedProduct();
			suggestedProduct.changeStatusPending();
		}

		tradeProductRepository.deleteAll(tradeProducts);

		// 이벤트 저장 및 전달
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
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		if (!(immediateTrade.isInProgress()) || !(reqDto.getTradeStatus() == TradeStatus.COMPLETED)) {
			throw new IllegalArgumentException("IN_PROGRESS 상태의 교환만을 COMPLETED 상태로 변경하실 수 있습니다.");
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

	public String cancelAcceptanceOfSuggest(Long tradeId, VerifiedMember member) {
		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		if (!(immediateTrade.isInProgress())) {
			throw new IllegalArgumentException("IN_PROGRESS 상태의 교환만을 수락 취소할 수 있습니다.");
		}

		immediateTrade.changeStatusPending();

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(tradeId,
			TradeType.IMMEDIATE);
		for (TradeProduct tradeProduct : tradeProducts) {
			tradeProduct.getSuggestedProduct().changeStatusPending();
			tradeProductRepository.delete(tradeProduct);
		}

		// 알림 (제안자에게)
		notificationService.saveTradeNotification(
			EventKind.IMMEDIATE_TRADE_SUGGEST_CANCEL, tradeProducts.get(0).getSuggestedProduct().getMember().getId(),
			TradeType.IMMEDIATE, immediateTrade.getId(), immediateTrade.getTitle()
		);

		return "추후 제안 다건 조회되도록 변경";
	}

	public List<FindSuggestForImmediateTradeResDto> findSuggestForImmediateTrade(
		Long tradeId, VerifiedMember member) {

		ImmediateTrade immediateTrade = immediateTradeRepository.findById(tradeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

		immediateTrade.validateAuthority(member.getId());

		List<TradeProduct> tradeProducts = tradeProductRepository.findAllByTradeIdAndTradeType(
			tradeId, TradeType.IMMEDIATE);

		List<FindSuggestForImmediateTradeResDto> resDtos = new ArrayList<>();

		for (TradeProduct tp : tradeProducts) {
			SuggestedProduct suggestedProduct = suggestedProductRepository.findById(tp.getSuggestedProduct().getId())
				.orElseThrow(() -> new IllegalArgumentException("제안 물품을 찾을 수 없습니다"));

			resDtos.add(FindSuggestForImmediateTradeResDto.from(suggestedProduct));
		}
		return resDtos;
	}
}