package com.barter.domain.trade.donationtrade.dto.response;

import com.barter.domain.trade.donationtrade.entity.DonationTrade;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateDonationTradeResDto {
	private final Long id;
	private final String title;
	private final String description;
	private final Long productId;

	@Builder
	public CreateDonationTradeResDto(Long id, String title, String description, Long productId) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.productId = productId;
	}

	public static CreateDonationTradeResDto from(DonationTrade trade) {
		return CreateDonationTradeResDto.builder()
			.id(trade.getId())
			.title(trade.getTitle())
			.description(trade.getDescription())
			.productId(trade.getProduct().getId())
			.build();
	}
}
