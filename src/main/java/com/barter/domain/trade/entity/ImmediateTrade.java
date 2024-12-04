package com.barter.domain.trade.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "IMMEDIATE_TRADES")
public class ImmediateTrade extends BaseTimeStampEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private RegisteredProduct product;
	@Enumerated(EnumType.STRING)
	private TradeStatus status;
	private int viewCount;
}
