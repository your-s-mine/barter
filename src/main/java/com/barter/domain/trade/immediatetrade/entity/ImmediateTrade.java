package com.barter.domain.trade.immediatetrade.entity;

import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.TradeCommonEntity;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "IMMEDIATE_TRADES")
public class ImmediateTrade extends TradeCommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder
	public ImmediateTrade(Long id, String title, String description, RegisteredProduct registeredProduct, TradeStatus status,
		int viewCount, String address1, String address2, Double longitude, Double latitude) {
		super(title, description, registeredProduct, status, viewCount, address1, address2, longitude, latitude);
		this.id = id;
	}
}
