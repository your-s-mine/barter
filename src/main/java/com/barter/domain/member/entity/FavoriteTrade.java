package com.barter.domain.member.entity;

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
@Table(name = "FAVORITE_TRADE")
public class FavoriteTrade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Member member;
	private Long tradeId;
	@Enumerated(EnumType.STRING)
	private TradeType tradeType;

	@Builder
	public FavoriteTrade(Long id, Member member, Long tradeId, TradeType tradeType) {
		this.id = id;
		this.member = member;
		this.tradeId = tradeId;
		this.tradeType = tradeType;
	}
}
