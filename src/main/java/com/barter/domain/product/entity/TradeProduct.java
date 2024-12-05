package com.barter.domain.product.entity;

import com.barter.domain.product.enums.TradeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TRADE_PRODUCTS")
public class TradeProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long tradeId;
	@Enumerated(EnumType.STRING)
	private TradeType tradeType;

	@ManyToOne
	private RegisteredProduct registeredProduct;

	@Builder
	protected TradeProduct(Long tradeId, TradeType tradeType, RegisteredProduct registeredProduct) {
		this.tradeId = tradeId;
		this.tradeType = tradeType;
		this.registeredProduct = registeredProduct;
	}

	public static TradeProduct createTradeProduct(Long tradeId, TradeType tradeType,
		RegisteredProduct registeredProduct) {
		return TradeProduct.builder()
			.tradeId(tradeId)
			.tradeType(tradeType)
			.registeredProduct(registeredProduct)
			.build();
	}
}
