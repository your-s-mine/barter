package com.barter.domain.member.entity;

import com.barter.domain.trade.enums.TradeStatus; // TradeStatus를 사용 (기존 TradeType 대체)
import jakarta.persistence.*;
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
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "trade_id", nullable = false)
	private Long tradeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "trade_status", nullable = false)
	private TradeStatus tradeStatus;

	@Builder
	public FavoriteTrade(Long id, Member member, Long tradeId, TradeStatus tradeStatus) {
		this.id = id;
		this.member = member;
		this.tradeId = tradeId;
		this.tradeStatus = tradeStatus;
	}
}
