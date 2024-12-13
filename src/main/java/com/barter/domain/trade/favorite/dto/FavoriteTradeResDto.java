package com.barter.domain.trade.favorite.dto;

import com.barter.domain.member.entity.FavoriteTrade;
import com.barter.domain.trade.enums.TradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FavoriteTradeResDto {
    private Long id;
    private Long memberId;
    private TradeStatus tradeStatus; // 필드명 수정
    private Long tradeId; // 필드명 수정

    @Builder
    public FavoriteTradeResDto(Long id, Long memberId, TradeStatus tradeStatus, Long tradeId) {
        this.id = id;
        this.memberId = memberId;
        this.tradeStatus = tradeStatus;
        this.tradeId = tradeId;
    }

    public static FavoriteTradeResDto from(FavoriteTrade favoriteTrade) {
        return FavoriteTradeResDto.builder()
                .id(favoriteTrade.getId())
                .memberId(favoriteTrade.getMember().getId())
                .tradeStatus(favoriteTrade.getTradeStatus()) // 수정된 필드명
                .tradeId(favoriteTrade.getTradeId()) // 수정된 필드명
                .build();
    }
}
