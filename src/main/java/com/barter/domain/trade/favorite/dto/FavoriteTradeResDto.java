package com.barter.domain.trade.favorite.dto;

import com.barter.domain.member.entity.FavoriteTrade;
import com.barter.domain.trade.enums.TradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteTradeResDto {
    private final Long id;
    private final Long memberId;
    private final TradeStatus tradeStatus; // 필드명 수정
    private final Long tradeId; // 필드명 수정

    public static FavoriteTradeResDto from(FavoriteTrade favoriteTrade) {
        return FavoriteTradeResDto.builder()
                .id(favoriteTrade.getId())
                .memberId(favoriteTrade.getMember().getId())
                .tradeStatus(favoriteTrade.getTradeStatus()) // 수정된 필드명
                .tradeId(favoriteTrade.getTradeId()) // 수정된 필드명
                .build();
    }
}
