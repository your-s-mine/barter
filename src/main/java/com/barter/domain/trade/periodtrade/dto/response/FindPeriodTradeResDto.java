package com.barter.domain.trade.periodtrade.dto.response;

import java.time.LocalDateTime;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindPeriodTradeResDto {
	private Long periodTradesId;
	private String title;
	private String description;
	private Long registeredProductId;
	@JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime createdAt;
	@JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime updatedAt;
	private TradeStatus tradeStatus;
	private int viewCount;
	@JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime endedAt;
	private String address1;
	private String address2;

	@Builder
	public FindPeriodTradeResDto(Long periodTradesId, String title, String description, RegisteredProduct product,
		LocalDateTime createdAt, LocalDateTime updatedAt, TradeStatus tradeStatus, int viewCount, LocalDateTime endedAt
		, String address1, String address2) {
		this.periodTradesId = periodTradesId;
		this.title = title;
		this.description = description;
		this.registeredProductId = product.getId();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.tradeStatus = tradeStatus;
		this.viewCount = viewCount;
		this.endedAt = endedAt;
		this.address1 = address1;
		this.address2 = address2;

	}

	public static FindPeriodTradeResDto from(PeriodTrade periodTrade) {
		return FindPeriodTradeResDto.builder()
			.periodTradesId(periodTrade.getId())
			.title(periodTrade.getTitle())
			.description(periodTrade.getDescription())
			.product(periodTrade.getRegisteredProduct())
			.createdAt(periodTrade.getCreatedAt())
			.updatedAt(periodTrade.getUpdatedAt())
			.tradeStatus(periodTrade.getStatus())
			.viewCount(periodTrade.getViewCount())
			.endedAt(periodTrade.getEndedAt())
			.address1(periodTrade.getAddress1())
			.address2(periodTrade.getAddress2())
			.build();
	}

}
