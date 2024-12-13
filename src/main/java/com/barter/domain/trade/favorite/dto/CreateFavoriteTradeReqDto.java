package com.barter.domain.trade.favorite.dto;

import com.barter.domain.trade.enums.TradeType;
import lombok.Getter;

@Getter
public class CreateFavoriteTradeReqDto {
    private TradeType tradesType;
    private Long tradesId;
}
