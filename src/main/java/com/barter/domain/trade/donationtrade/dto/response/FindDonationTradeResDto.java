package com.barter.domain.trade.donationtrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.dto.response.FindRegisteredProductResDto;
import com.barter.domain.trade.donationtrade.entity.DonationTrade;
import com.barter.domain.trade.enums.TradeStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindDonationTradeResDto {
	private Long id;
	private String title;
	private String description;
	private FindRegisteredProductResDto productResDto;
	private TradeStatus status;
	private Integer viewCount;
	private Integer maxAmount;
	private Integer currentAmount;
	private LocalDateTime createdAt;
	private LocalDateTime endedAt;

	@Builder
	public FindDonationTradeResDto(Long id, String title, String description, FindRegisteredProductResDto productResDto,
		TradeStatus status, Integer viewCount, Integer maxAmount, Integer currentAmount, LocalDateTime createdAt,
		LocalDateTime endedAt) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.productResDto = productResDto;
		this.status = status;
		this.viewCount = viewCount;
		this.maxAmount = maxAmount;
		this.currentAmount = currentAmount;
		this.createdAt = createdAt;
		this.endedAt = endedAt;
	}

	public static FindDonationTradeResDto from(DonationTrade donationTrade) {
		return FindDonationTradeResDto.builder()
			.id(donationTrade.getId())
			.title(donationTrade.getTitle())
			.description(donationTrade.getDescription())
			.status(donationTrade.getStatus())
			.viewCount(donationTrade.getViewCount())
			.productResDto(FindRegisteredProductResDto.from(donationTrade.getProduct()))
			.maxAmount(donationTrade.getMaxAmount())
			.currentAmount(donationTrade.getCurrentAmount())
			.createdAt(donationTrade.getCreatedAt())
			.endedAt(donationTrade.getEndedAt())
			.build();
	}
}
