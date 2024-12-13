package com.barter.domain.trade.favorite.dto;

import com.barter.domain.trade.favorite.entity.FavoriteTrade;
import com.barter.domain.trade.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FavoriteTradeResDto {
    private Long id;
    private Long memberId;
    private TradeType tradesType;
    private Long tradesId;

    @Builder
    public FavoriteTradeResDto(Long id, Long memberId, TradeType tradesType, Long tradesId) {
        this.id = id;
        this.memberId = memberId;
        this.tradesType = tradesType;
        this.tradesId = tradesId;
    }

    public static FavoriteTradeResDto from(FavoriteTrade favoriteTrade) {
        return FavoriteTradeResDto.builder()
                .id(favoriteTrade.getId())
                .memberId(favoriteTrade.getMember().getId())
                .tradesType(favoriteTrade.getTradesType())
                .tradesId(favoriteTrade.getTradesId())
                .build();
    }
}
