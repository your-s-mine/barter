package com.barter.domain.trade.favorite.dto;

import com.barter.domain.trade.enums.TradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateFavoriteTradeReqDto {
    private final TradeStatus tradeStatus; // 필드명 수정
    private final Long tradeId; // 필드명 수정
}
