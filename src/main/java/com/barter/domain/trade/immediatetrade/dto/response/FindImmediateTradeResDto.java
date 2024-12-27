package com.barter.domain.trade.immediatetrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindImmediateTradeResDto {

	private String title;
	private String description;
	private Long registerProductId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	private String address1;
	private String address2;
	private Double longitude;
	private Double latitude;

	@Builder
	public FindImmediateTradeResDto(String title, String description, RegisteredProduct registeredProduct,
		LocalDateTime createdAt,
		LocalDateTime updatedAt, TradeStatus tradeStatus, int viewCount, String address1, String address2,
		Double longitude,
		Double latitude) {
		this.title = title;
		this.description = description;
		this.registerProductId = registeredProduct.getId();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
		this.address1 = address1;
		this.address2 = address2;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public static FindImmediateTradeResDto from(ImmediateTrade immediateTrade) {
		return FindImmediateTradeResDto.builder()
			.title(immediateTrade.getTitle())
			.description(immediateTrade.getDescription())
			.registeredProduct(immediateTrade.getRegisteredProduct())
			.createdAt(immediateTrade.getCreatedAt())
			.updatedAt(immediateTrade.getUpdatedAt())
			.tradeStatus(immediateTrade.getStatus())
			.viewCount(immediateTrade.getViewCount())
			.address1(immediateTrade.getAddress1())
			.address2(immediateTrade.getAddress2())
			.latitude(immediateTrade.getLatitude())
			.longitude(immediateTrade.getLongitude())
			.build();
	}
}
