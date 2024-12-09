package com.barter.domain.trade.common.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.trade.enums.TradeStatus;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseTradeEntity extends BaseTimeStampEntity {
	private String title;
	@ManyToOne
	@JoinColumn(name = "product_id")
	private RegisteredProduct registeredProduct;
	private TradeStatus tradeStatus;
	private int viewCount;
}
