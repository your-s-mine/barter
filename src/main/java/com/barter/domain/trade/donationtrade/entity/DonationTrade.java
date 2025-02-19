package com.barter.domain.trade.donationtrade.entity;

import static com.barter.exception.enums.ExceptionCode.*;

import java.time.LocalDateTime;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.exception.customexceptions.DonationTradeException;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DONATION_TRADES", indexes = {
	@Index(name = "idx_updated_at", columnList = "updated_at")
})
public class DonationTrade extends BaseTimeStampEntity {

	private static final int MAX_AFTER_DAY = 7;
	private static final int MIN_LENGTH = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	@ManyToOne(fetch = FetchType.LAZY)
	private RegisteredProduct product;
	@Enumerated(EnumType.STRING)
	private TradeStatus status;
	private int viewCount;
	private int maxAmount;
	private int currentAmount;
	private LocalDateTime endedAt;

	@Builder
	public DonationTrade(Long id, String title, String description, RegisteredProduct product, TradeStatus status,
		int viewCount, int maxAmount, int currentAmount, LocalDateTime endedAt) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.product = product;
		this.status = status;
		this.viewCount = viewCount;
		this.maxAmount = maxAmount;
		this.currentAmount = currentAmount;
		this.endedAt = endedAt;
	}

	public static DonationTrade createInitDonationTrade(RegisteredProduct product, Integer maxAmount, String title,
		String description, LocalDateTime endedAt
	) {
		product.changeStatusRegistering();
		return DonationTrade.builder()
			.title(title)
			.description(description)
			.maxAmount(maxAmount)
			.product(product)
			.viewCount(0)
			.currentAmount(0)
			.status(TradeStatus.IN_PROGRESS)
			.endedAt(endedAt)
			.build();
	}

	public void validateExceedMaxEndedAt() {
		if (endedAt.minusDays(MAX_AFTER_DAY).isAfter(LocalDateTime.now())) {
			throw new DonationTradeException(INVALID_END_DATE);
		}
	}

	public void validateUpdate(Long userId) {
		product.validateOwner(userId);
		if (currentAmount > 0) {
			throw new DonationTradeException(DUPLICATE_REQUEST);
		}
	}

	public void update(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public void validateDelete(Long userId) {
		product.validateOwner(userId);
		if (status.equals(TradeStatus.COMPLETED) || maxAmount == currentAmount) {
			throw new DonationTradeException(ALREADY_CLOSED);
		}
	}

	public boolean isDonationCompleted() {
		return status == TradeStatus.COMPLETED || maxAmount == currentAmount;
	}

	public void suggestDonation() {
		currentAmount++;
		if (currentAmount == maxAmount) {
			product.changeStatusCompleted();
			status = TradeStatus.COMPLETED;
		}
	}

	public void changeProductStatusPending() {
		product.changeStatusPending();
	}
}
