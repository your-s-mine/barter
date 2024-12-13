package com.barter.domain.trade.favorite.entity;

import com.barter.domain.member.entity.Member;
import com.barter.domain.trade.enums.TradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FAV_DONATION")
public class FavoriteTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "trades_type", nullable = false)
    private TradeType tradesType;

    @Column(name = "trades_id", nullable = false)
    private Long tradesId;

    @Builder
    private FavoriteTrade(Member member, TradeType tradesType, Long tradesId) {
        this.member = member;
        this.tradesType = tradesType;
        this.tradesId = tradesId;
    }
}
