package com.barter.domain.trade.favorite.dto;

import com.barter.domain.trade.enums.TradeStatus;
import lombok.Getter;

@Getter
public class CreateFavoriteTradeReqDto {
    private TradeStatus tradeStatus; // 필드명 수정
    private Long tradeId; // 필드명 수정
}
